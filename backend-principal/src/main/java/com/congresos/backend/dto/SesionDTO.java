package com.congresos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferir información de sesiones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionDTO {

    private Long idSesion;
    private String titulo;
    private String descripcion;
    private String ponente;
    private String sala;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer aforoMaximo;
    private Integer aforoActual;
    private Integer plazasDisponibles;
    private Boolean activa;
}
