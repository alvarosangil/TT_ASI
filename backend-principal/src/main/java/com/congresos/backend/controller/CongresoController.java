package com.congresos.backend.controller;

import com.congresos.backend.dto.CongresoDTO;
import com.congresos.backend.dto.CongresoRequest;
import com.congresos.backend.service.CongresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de congresos
 * CU12: Buscar y filtrar congresos
 * CU19: Ver detalle de congreso
 * CU23: Crear congreso (ORGANIZADOR)
 * CU24: Editar congreso (ORGANIZADOR)
 */
@RestController
@RequestMapping("/api/congresos")
@RequiredArgsConstructor
public class CongresoController {

    private final CongresoService congresoService;

    /**
     * CU12: Buscar y filtrar congresos (público)
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CongresoDTO>> buscarCongresos(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tematica
    ) {
        List<CongresoDTO> congresos = congresoService.buscarCongresos(query, ciudad, tematica);
        return ResponseEntity.ok(congresos);
    }

    /**
     * CU19: Ver detalle de un congreso (público)
     */
    @GetMapping("/{id}/detalle")
    public ResponseEntity<CongresoDTO> obtenerDetalleCongreso(@PathVariable Long id) {
        CongresoDTO congreso = congresoService.obtenerDetalleCongreso(id);
        return ResponseEntity.ok(congreso);
    }

    /**
     * CU3: Ver programa del congreso (público)
     */
    @GetMapping("/{id}/programa")
    public ResponseEntity<CongresoDTO> obtenerProgramaCongreso(@PathVariable Long id) {
        CongresoDTO congreso = congresoService.obtenerProgramaCompleto(id);
        return ResponseEntity.ok(congreso);
    }

    /**
     * CU23: Crear congreso (solo ORGANIZADOR)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<CongresoDTO> crearCongreso(
            @Valid @RequestBody CongresoRequest request,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        CongresoDTO congreso = congresoService.crearCongreso(request, emailOrganizador);
        return ResponseEntity.status(HttpStatus.CREATED).body(congreso);
    }

    /**
     * CU24: Editar congreso (solo ORGANIZADOR propietario)
     */
    @PutMapping("/{id}/editar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<CongresoDTO> editarCongreso(
            @PathVariable Long id,
            @Valid @RequestBody CongresoRequest request,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        CongresoDTO congreso = congresoService.editarCongreso(id, request, emailOrganizador);
        return ResponseEntity.ok(congreso);
    }

    /**
     * Obtener congresos del organizador autenticado
     */
    @GetMapping("/mis-congresos")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<CongresoDTO>> obtenerMisCongresos(Authentication authentication) {
        String emailOrganizador = authentication.getName();
        List<CongresoDTO> congresos = congresoService.obtenerCongresosPorOrganizador(emailOrganizador);
        return ResponseEntity.ok(congresos);
    }

    /**
     * CU25: Eliminar congreso (solo ORGANIZADOR propietario)
     */
    @DeleteMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<Void> eliminarCongreso(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailOrganizador = authentication.getName();
        congresoService.eliminarCongreso(id, emailOrganizador);
        return ResponseEntity.noContent().build();
    }
}
