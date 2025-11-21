package com.congresos.backend.repository;

import com.congresos.backend.model.domain.InscripcionCongreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad InscripcionCongreso
 * CU4: Inscribirse a un congreso
 * CU6: Ver Mis Congresos
 */
@Repository
public interface InscripcionCongresoRepository extends JpaRepository<InscripcionCongreso, Long> {

    /**
     * Busca inscripciones activas de un asistente
     */
    List<InscripcionCongreso> findByAsistente_IdUsuarioAndActivaTrue(Long idAsistente);

    /**
     * Busca inscripción de un asistente a un congreso específico
     */
    Optional<InscripcionCongreso> findByAsistente_IdUsuarioAndCongreso_IdCongreso(Long idAsistente, Long idCongreso);

    /**
     * Verifica si un asistente ya está inscrito en un congreso
     */
    boolean existsByAsistente_IdUsuarioAndCongreso_IdCongresoAndActivaTrue(Long idAsistente, Long idCongreso);

    /**
     * Cuenta las inscripciones confirmadas de un congreso
     */
    @Query("SELECT COUNT(i) FROM InscripcionCongreso i WHERE i.congreso.idCongreso = :idCongreso AND i.estado = 'CONFIRMADA' AND i.activa = true")
    Long countInscripcionesConfirmadas(@Param("idCongreso") Long idCongreso);
}
