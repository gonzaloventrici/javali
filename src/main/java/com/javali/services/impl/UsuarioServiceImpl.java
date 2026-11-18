package com.javali.services.impl;

import com.javali.dtos.UsuarioRequest;
import com.javali.dtos.UsuarioResponse;
import com.javali.exceptions.EmailYaRegistradoException;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.RolUsuario;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Recibe el repositorio y el codificador de contraseñas.

    @Override
    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        usuario.setRol(RolUsuario.ESTUDIANTE);

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        return aRespuesta(usuarioGuardado);
    }
    // Registra un estudiante y guarda su contraseña protegida.

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(id)
                );

        return aRespuesta(usuario);
    }
    // Busca un usuario por id o informa que no existe.

    private UsuarioResponse aRespuesta(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
    // Convierte la entidad en un DTO sin mostrar la contraseña.
}

//UsuarioRequest
//→ verifica el email
//→ codifica la contraseña
//→ asigna el rol ESTUDIANTE
//→ guarda el usuario
//→ devuelve UsuarioResponse