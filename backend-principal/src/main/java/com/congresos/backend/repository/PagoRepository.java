package com.congresos.backend.repository;

import com.congresos.backend.model.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Pago
 * CU5: Pago de inscripción al congreso
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Busca pago por ID de inscripción
     */
    Optional<Pago> findByInscripcionCongreso_IdInscripcion(Long idInscripcion);

    /**
     * Busca pago por ID de transacción
     */
    Optional<Pago> findByTransaccionId(String transaccionId);
}
