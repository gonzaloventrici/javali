package com.javali.services.impl;

import com.javali.dtos.PreferenciasRequest;
import com.javali.dtos.PreferenciasResponse;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.PreferenciasUsuario;
import com.javali.models.Usuario;
import com.javali.repositories.PreferenciasUsuarioRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.PreferenciasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenciasServiceImpl implements PreferenciasService {

    private final PreferenciasUsuarioRepository preferenciasRepository;
    private final UsuarioRepository usuarioRepository;

    public PreferenciasServiceImpl(
            PreferenciasUsuarioRepository preferenciasRepository,
            UsuarioRepository usuarioRepository) {
        this.preferenciasRepository = preferenciasRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PreferenciasResponse obtener(Long usuarioId) {
        return preferenciasRepository
                .findByUsuario_Id(usuarioId)
                .map(this::aRespuesta)
                .orElseGet(PreferenciasResponse::porDefecto);
    }

    @Override
    @Transactional
    public PreferenciasResponse guardar(
            Long usuarioId,
            PreferenciasRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(usuarioId)
                );
        PreferenciasUsuario preferencias = preferenciasRepository
                .findByUsuario_Id(usuarioId)
                .orElseGet(() -> {
                    PreferenciasUsuario nuevas = new PreferenciasUsuario();
                    nuevas.setUsuario(usuario);
                    return nuevas;
                });
        preferencias.setTamanoLetra(request.tamanoLetra());
        preferencias.setContraste(request.contraste());
        preferencias.setTipografia(request.tipografia());
        preferencias.setMostrarRacha(request.mostrarRacha());
        return aRespuesta(preferenciasRepository.save(preferencias));
    }

    private PreferenciasResponse aRespuesta(PreferenciasUsuario preferencias) {
        return new PreferenciasResponse(
                preferencias.getTamanoLetra(),
                preferencias.getContraste(),
                preferencias.getTipografia(),
                preferencias.isMostrarRacha()
        );
    }
    // Convierte la entidad en un DTO de respuesta.
}
