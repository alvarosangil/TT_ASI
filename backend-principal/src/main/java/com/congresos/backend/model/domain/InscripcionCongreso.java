package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad InscripcionCongreso - Representa la inscripción de un asistente a un congreso
 * CU4: Inscribirse a un congreso
 * CU15: Cancelar inscripción a un congreso
 */
@Entity
@Table(name = "inscripciones_congreso",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_asistente", "id_congreso"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionCongreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asistente", nullable = false)
    private Usuario asistente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_congreso", nullable = false)
    private Congreso congreso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado = EstadoInscripcion.PENDIENTE;

    @OneToOne(mappedBy = "inscripcionCongreso", cascade = CascadeType.ALL)
    private Pago pago;

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

    // Enum para estados de inscripción
    public enum EstadoInscripcion {
        PENDIENTE,      // Esperando pago (si es de pago)
        CONFIRMADA,     // Inscripción confirmada
        CANCELADA       // Cancelada por el usuario
    }

    /**
     * Confirmar la inscripción (tras pago o si es gratuito)
     */
    public void confirmar() {
        this.estado = EstadoInscripcion.CONFIRMADA;
    }

    /**
     * Cancelar la inscripción
     */
    public void cancelar() {
        this.estado = EstadoInscripcion.CANCELADA;
        this.activa = false;
        this.fechaCancelacion = LocalDateTime.now();
    }
}
