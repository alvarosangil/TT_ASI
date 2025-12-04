package com.congresos.backend.controller;

import com.congresos.backend.dto.SesionDTO;
import com.congresos.backend.dto.SesionRequest;
import com.congresos.backend.service.SesionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de sesiones
 * CU26: Crear sesión (ORGANIZADOR)
 * CU27: Editar sesión (ORGANIZADOR)
 * CU28: Eliminar sesión (ORGANIZADOR)
 * CU4: Listar sesiones de un congreso (público)
 */
@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    /**
     * CU4: Listar sesiones de un congreso (público)
     */
    @GetMapping("/congreso/{idCongreso}")
    public ResponseEntity<List<SesionDTO>> listarSesionesCongreso(@PathVariable Long idCongreso) {
        List<SesionDTO> sesiones = sesionService.obtenerSesionesPorCongreso(idCongreso);
        return ResponseEntity.ok(sesiones);
    }

    /**
     * CU26: Crear sesión en un congreso (solo ORGANIZADOR propietario)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<SesionDTO> crearSesion(
            @Valid @RequestBody SesionRequest request,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        SesionDTO sesion = sesionService.crearSesion(request, emailOrganizador);
        return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
    }

    /**
     * CU27: Editar sesión (solo ORGANIZADOR propietario del congreso)
     */
    @PutMapping("/{id}/editar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<SesionDTO> editarSesion(
            @PathVariable Long id,
            @Valid @RequestBody SesionRequest request,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        SesionDTO sesion = sesionService.editarSesion(id, request, emailOrganizador);
        return ResponseEntity.ok(sesion);
    }

    /**
     * CU28: Eliminar sesión (solo ORGANIZADOR propietario del congreso)
     */
    @DeleteMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<Void> eliminarSesion(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        sesionService.eliminarSesion(id, emailOrganizador);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener detalle de una sesión específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<SesionDTO> obtenerSesion(@PathVariable Long id) {
        SesionDTO sesion = sesionService.obtenerSesionPorId(id);
        return ResponseEntity.ok(sesion);
    }
}
