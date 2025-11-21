package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Pago - Representa el pago de una inscripción a un congreso
 * CU5: Pago de inscripción al congreso
 */
@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion", nullable = false, unique = true)
    private InscripcionCongreso inscripcionCongreso;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private MetodoPago metodoPago;

    @Column(length = 100)
    private String transaccionId; // ID de la pasarela de pago

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    private LocalDateTime fechaConfirmacion;

    // Enums
    public enum EstadoPago {
        PENDIENTE,
        PROCESANDO,
        COMPLETADO,
        FALLIDO,
        REEMBOLSADO
    }

    public enum MetodoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        PAYPAL,
        TRANSFERENCIA,
        BIZUM
    }

    /**
     * Confirmar el pago
     */
    public void confirmar() {
        this.estado = EstadoPago.COMPLETADO;
        this.fechaConfirmacion = LocalDateTime.now();
    }

    /**
     * Marcar pago como fallido
     */
    public void marcarFallido() {
        this.estado = EstadoPago.FALLIDO;
    }
}
