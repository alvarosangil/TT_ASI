package com.congresos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de login/registro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long idUsuario;
    private String email;
    private String nombreCompleto;
    private String tipoUsuario;
}
