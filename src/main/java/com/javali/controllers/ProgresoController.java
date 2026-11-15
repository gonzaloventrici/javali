package com.javali.controllers;

import com.javali.dtos.ProgresoResponse;
import com.javali.services.ProgresoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javali.dtos.ResumenProgresoResponse;
import com.javali.dtos.EstadoLeccionResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProgresoController {

    private final ProgresoService progresoService;

    public ProgresoController(ProgresoService progresoService) {
        this.progresoService = progresoService;
    }

    // Recibe el servicio mediante inyección por constructor.

    @GetMapping("/usuarios/{usuarioId}/progresos")
    public ResponseEntity<List<ProgresoResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                progresoService.listarPorUsuario(usuarioId)
        );
    }
    // Devuelve el progreso del usuario en sus lecciones.

    @GetMapping("/usuarios/{usuarioId}/progreso/resumen")
    public ResponseEntity<ResumenProgresoResponse> obtenerResumen(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                progresoService.obtenerResumen(usuarioId)
        );
    }
    // Devuelve fortalezas, temas pendientes y una recomendación.

    @GetMapping("/usuarios/{usuarioId}/lecciones/{leccionId}/estado")
    public ResponseEntity<EstadoLeccionResponse> obtenerEstadoLeccion(
            @PathVariable Long usuarioId,
            @PathVariable Long leccionId) {
        return ResponseEntity.ok(
                progresoService.obtenerEstadoLeccion(
                        usuarioId,
                        leccionId
                )
        );
    }
    // Devuelve si la lección está desbloqueada y completada.
}