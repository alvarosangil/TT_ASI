package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad InscripcionSesion - Representa la inscripción de un asistente a una sesión
 * CU7: Inscribirse a una sesión
 * CU16: Cancelar inscripción a una sesión
 */
@Entity
@Table(name = "inscripciones_sesion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_asistente", "id_sesion"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionSesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcionSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asistente", nullable = false)
    private Usuario asistente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion", nullable = false)
    private Sesion sesion;

    @Column(length = 500)
    private String ticketQR;

    @Column(length = 100)
    private String ticketId; // ID del ticket generado por el servicio externo

    @Column(nullable = false)
    private Boolean activa = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaCancelacion;

    /**
     * Cancelar la inscripción a la sesión
     */
    public void cancelar() {
        this.activa = false;
        this.fechaCancelacion = LocalDateTime.now();
    }
}
