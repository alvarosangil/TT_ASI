package com.congresos.backend.repository;

import com.congresos.backend.model.domain.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Sesion
 * CU3: Ver programa de un congreso
 */
@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /**
     * Busca sesiones activas de un congreso
     */
    List<Sesion> findByCongreso_IdCongresoAndActivaTrueOrderByFechaHoraInicio(Long idCongreso);

    /**
     * Busca sesiones por sala
     */
    List<Sesion> findBySalaAndActivaTrue(String sala);

    /**
     * Busca sesiones con conflicto de horario
     */
    @Query("SELECT s FROM Sesion s WHERE s.congreso.idCongreso = :idCongreso " +
           "AND s.activa = true " +
           "AND s.fechaHoraInicio < :fechaFin " +
           "AND s.fechaHoraFin > :fechaInicio")
    List<Sesion> findSesionesConConflicto(
        @Param("idCongreso") Long idCongreso,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
