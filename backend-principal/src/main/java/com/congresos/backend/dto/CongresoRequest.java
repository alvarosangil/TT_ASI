package com.congresos.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear/editar congresos
 * CU23: Crear Congreso
 * CU24: Editar Congreso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongresoRequest {

    @NotBlank(message = "El nombre del congreso es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fechaFin;

    @NotBlank(message = "El lugar es obligatorio")
    @Size(max = 200, message = "El lugar no puede exceder 200 caracteres")
    private String lugar;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "Debe indicar si el congreso es de pago")
    private Boolean esPago;

    @Size(max = 255, message = "La URL de la imagen no puede exceder 255 caracteres")
    private String imagenPortada;

    @Size(max = 100, message = "La temática no puede exceder 100 caracteres")
    private String tematica;
}
