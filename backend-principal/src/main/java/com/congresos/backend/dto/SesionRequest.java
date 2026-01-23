package com.congresos.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionRequest {
    
    @NotNull(message = "El ID del congreso es obligatorio")
    private Long idCongreso;
    
    @NotBlank(message = "El título de la sesión es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;
    
    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;
    
    @Size(max = 200, message = "El nombre del ponente no puede exceder 200 caracteres")
    private String ponente;
    
    @Size(max = 100, message = "La sala no puede exceder 100 caracteres")
    private String sala;
    
    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    private LocalDateTime fechaHoraInicio;
    
    @NotNull(message = "La fecha y hora de fin son obligatorias")
    private LocalDateTime fechaHoraFin;
    
    @Min(value = 1, message = "El aforo máximo debe ser al menos 1")
    @Max(value = 10000, message = "El aforo máximo no puede exceder 10000")
    private Integer aforoMaximo;
}
