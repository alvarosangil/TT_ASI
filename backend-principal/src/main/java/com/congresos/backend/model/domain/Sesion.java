package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Sesion - Representa una sesión dentro de un congreso
 * Las sesiones tienen sala, horario y aforo
 */
@Entity
@Table(name = "sesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_congreso", nullable = false)
    private Congreso congreso;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String ponente;

    @Column(nullable = false, length = 100)
    private String sala;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false)
    private Integer aforoMaximo;

    @Column(nullable = false)
    private Integer aforoActual = 0;

    @Column(nullable = false)
    private Boolean activa = true;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscripcionSesion> inscripciones = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Verifica si hay plazas disponibles
     */
    public boolean hayPlazasDisponibles() {
        return this.aforoActual < this.aforoMaximo;
    }

    /**
     * Incrementa el aforo actual
     */
    public void incrementarAforo() {
        if (hayPlazasDisponibles()) {
            this.aforoActual++;
        } else {
            throw new IllegalStateException("No hay plazas disponibles en esta sesión");
        }
    }

    /**
     * Decrementa el aforo actual
     */
    public void decrementarAforo() {
        if (this.aforoActual > 0) {
            this.aforoActual--;
        }
    }

    /**
     * Verifica si hay conflicto de horario con otra sesión
     */
    public boolean tieneConflictoHorarioCon(Sesion otraSesion) {
        return !(this.fechaHoraFin.isBefore(otraSesion.fechaHoraInicio) || 
                 this.fechaHoraInicio.isAfter(otraSesion.fechaHoraFin));
    }
}
