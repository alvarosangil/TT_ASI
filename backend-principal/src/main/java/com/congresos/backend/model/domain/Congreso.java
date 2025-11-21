package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Congreso - Representa un congreso académico
 * CU23: Crear Congreso
 */
@Entity
@Table(name = "congresos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Congreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCongreso;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, length = 200)
    private String lugar;

    @Column(length = 100)
    private String ciudad;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Boolean esPago = false;

    @Column(length = 255)
    private String imagenPortada;

    @Column(length = 100)
    private String tematica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizador", nullable = false)
    private Usuario organizador;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "congreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sesion> sesiones = new ArrayList<>();

    @OneToMany(mappedBy = "congreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscripcionCongreso> inscripciones = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Verifica si el congreso ya ha finalizado
     * @return true si la fecha actual es mayor a la fecha de fin
     */
    public boolean haFinalizado() {
        return LocalDate.now().isAfter(this.fechaFin);
    }

    /**
     * Verifica si el congreso está en curso
     */
    public boolean estaEnCurso() {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(this.fechaInicio) && !hoy.isAfter(this.fechaFin);
    }
}
