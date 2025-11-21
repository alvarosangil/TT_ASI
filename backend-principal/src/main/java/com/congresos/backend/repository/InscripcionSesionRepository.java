package com.congresos.backend.repository;

import com.congresos.backend.model.domain.InscripcionSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad InscripcionSesion
 * CU7: Inscribirse a una sesión
 * CU8: Ver Mis Sesiones
 */
@Repository
public interface InscripcionSesionRepository extends JpaRepository<InscripcionSesion, Long> {

    /**
     * Busca inscripciones activas de un asistente
     */
    List<InscripcionSesion> findByAsistente_IdUsuarioAndActivaTrueOrderBySesion_FechaHoraInicio(Long idAsistente);

    /**
     * Busca inscripciones de un asistente a sesiones de un congreso específico
     */
    @Query("SELECT i FROM InscripcionSesion i WHERE i.asistente.idUsuario = :idAsistente " +
           "AND i.sesion.congreso.idCongreso = :idCongreso AND i.activa = true")
    List<InscripcionSesion> findByAsistenteAndCongreso(@Param("idAsistente") Long idAsistente, 
                                                        @Param("idCongreso") Long idCongreso);

    /**
     * Verifica si un asistente ya está inscrito en una sesión
     */
    boolean existsByAsistente_IdUsuarioAndSesion_IdSesionAndActivaTrue(Long idAsistente, Long idSesion);

    /**
     * Busca inscripción específica
     */
    Optional<InscripcionSesion> findByAsistente_IdUsuarioAndSesion_IdSesion(Long idAsistente, Long idSesion);

    /**
     * Busca sesiones con conflicto de horario para un asistente
     */
    @Query("SELECT i FROM InscripcionSesion i WHERE i.asistente.idUsuario = :idAsistente " +
           "AND i.activa = true " +
           "AND i.sesion.fechaHoraInicio < :fechaFin " +
           "AND i.sesion.fechaHoraFin > :fechaInicio")
    List<InscripcionSesion> findSesionesConConflictoParaAsistente(
        @Param("idAsistente") Long idAsistente,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
