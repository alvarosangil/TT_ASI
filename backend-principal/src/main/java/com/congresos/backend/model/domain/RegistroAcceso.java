package com.congresos.backend.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad RegistroAcceso - Registra los escaneos de QR en puertas y salas
 * CU9: Escaneo y control de acceso
 */
@Entity
@Table(name = "registros_acceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_congreso")
    private Congreso congreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion")
    private Sesion sesion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAcceso tipoAcceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoEscaneo resultado;

    @Column(length = 100)
    private String gateId; // Identificador de la puerta/sala

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_staff")
    private Usuario staff; // Staff que realizó el escaneo

    @Column(length = 500)
    private String qrEscaneado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaHoraEscaneo;

    // Enums
    public enum TipoAcceso {
        CONGRESO,
        SESION
    }

    public enum ResultadoEscaneo {
        VALIDO,
        YA_USADO,
        FALSIFICADO,
        CADUCADO,
        NO_VALIDO_SALA,
        NO_VALIDO_HORARIO
    }
}
