package com.javali.services.impl;

import com.javali.dtos.PreferenciasRequest;
import com.javali.dtos.PreferenciasResponse;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.NivelContraste;
import com.javali.models.PreferenciasUsuario;
import com.javali.models.TamanoLetra;
import com.javali.models.Tipografia;
import com.javali.models.Usuario;
import com.javali.repositories.PreferenciasUsuarioRepository;
import com.javali.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenciasServiceImplTest {

    @Mock
    private PreferenciasUsuarioRepository preferenciasRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PreferenciasServiceImpl servicio;

    @BeforeEach
    void preparar() {
        servicio = new PreferenciasServiceImpl(
                preferenciasRepository,
                usuarioRepository
        );
    }

    @Test
    void sinAjustesGuardadosDevuelveLosValoresPorDefecto() {
        when(preferenciasRepository.findByUsuario_Id(1L))
                .thenReturn(Optional.empty());

        PreferenciasResponse respuesta = servicio.obtener(1L);

        assertEquals(TamanoLetra.NORMAL, respuesta.tamanoLetra());
        assertEquals(NivelContraste.NORMAL, respuesta.contraste());
        assertEquals(Tipografia.SISTEMA, respuesta.tipografia());
        assertTrue(respuesta.mostrarRacha());
    }

    @Test
    void guardarCreaLosAjustesSiNoExistian() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(preferenciasRepository.findByUsuario_Id(1L))
                .thenReturn(Optional.empty());
        when(preferenciasRepository.save(any(PreferenciasUsuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        PreferenciasResponse respuesta = servicio.guardar(
                1L,
                new PreferenciasRequest(
                        TamanoLetra.GRANDE,
                        NivelContraste.ALTO,
                        Tipografia.DISLEXIA,
                        false
                )
        );

        ArgumentCaptor<PreferenciasUsuario> captor =
                ArgumentCaptor.forClass(PreferenciasUsuario.class);
        verify(preferenciasRepository).save(captor.capture());
        assertEquals(usuario, captor.getValue().getUsuario());
        assertEquals(TamanoLetra.GRANDE, respuesta.tamanoLetra());
        assertEquals(NivelContraste.ALTO, respuesta.contraste());
        assertEquals(Tipografia.DISLEXIA, respuesta.tipografia());
        assertFalse(respuesta.mostrarRacha());
    }

    @Test
    void guardarActualizaLosAjustesExistentes() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        PreferenciasUsuario existentes = new PreferenciasUsuario();
        existentes.setUsuario(usuario);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(preferenciasRepository.findByUsuario_Id(1L))
                .thenReturn(Optional.of(existentes));
        when(preferenciasRepository.save(existentes))
                .thenReturn(existentes);

        servicio.guardar(
                1L,
                new PreferenciasRequest(
                        TamanoLetra.MUY_GRANDE,
                        NivelContraste.NORMAL,
                        Tipografia.LEGIBLE,
                        true
                )
        );

        assertEquals(TamanoLetra.MUY_GRANDE, existentes.getTamanoLetra());
        assertEquals(Tipografia.LEGIBLE, existentes.getTipografia());
    }

    @Test
    void guardarParaUsuarioInexistenteLanzaExcepcion() {
        when(usuarioRepository.findById(9L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> servicio.guardar(
                        9L,
                        new PreferenciasRequest(
                                TamanoLetra.NORMAL,
                                NivelContraste.NORMAL,
                                Tipografia.SISTEMA,
                                true
                        )
                )
        );
    }
}
