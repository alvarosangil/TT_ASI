package com.congresos.backend.controller;

import com.congresos.backend.dto.LoginRequest;
import com.congresos.backend.dto.LoginResponse;
import com.congresos.backend.dto.RegistroRequest;
import com.congresos.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación
 * CU1: Registro de Asistente
 * CU2: Inicio de sesión
 * CU22: Cerrar sesión
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * CU1: Registro de nuevo usuario
     */
    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        LoginResponse response = authService.registrar(request);
        return ResponseEntity.ok(response);
    }

    /**
     * CU2: Inicio de sesión
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
