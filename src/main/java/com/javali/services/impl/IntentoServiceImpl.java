package com.javali.services.impl;

import com.javali.dtos.IntentoResponse;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Intento;
import com.javali.repositories.IntentoRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.IntentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntentoServiceImpl implements IntentoService {

    private final IntentoRepository intentoRepository;
    private final UsuarioRepository usuarioRepository;

    public IntentoServiceImpl(
            IntentoRepository intentoRepository,
            UsuarioRepository usuarioRepository) {

        this.intentoRepository = intentoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional(readOnly = true)
    public List<IntentoResponse> listarPorUsuario(Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        return intentoRepository
                .findByUsuario_IdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Devuelve los intentos del usuario del más reciente al más antiguo.

    private IntentoResponse aRespuesta(Intento intento) {

        return new IntentoResponse(
                intento.getId(),
                intento.getUsuario().getId(),
                intento.getEjercicio().getId(),
                intento.getIndiceSeleccionado(),
                intento.isCorrecta(),
                intento.getCategoriaError(),
                intento.getFecha()
        );
    }
    // Convierte la entidad Intento en un DTO de respuesta.
}