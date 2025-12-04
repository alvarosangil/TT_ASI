package com.congresos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferir información de congresos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongresoDTO {

    private Long idCongreso;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String lugar;
    private String ciudad;
    private BigDecimal precio;
    private Boolean esPago;
    private String imagenPortada;
    private String tematica;
    private Boolean activo;
    private String nombreOrganizador;
    private LocalDateTime fechaCreacion;
    
    // Para CU3: Ver programa
    private List<SesionDTO> sesiones;
    
    // Información adicional útil
    private Boolean haFinalizado;
    private Boolean estaEnCurso;
}
