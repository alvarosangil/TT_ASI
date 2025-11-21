package com.congresos.backend.repository;

import com.congresos.backend.model.domain.Congreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Congreso
 * CU12: Buscar y filtrar congresos
 */
@Repository
public interface CongresoRepository extends JpaRepository<Congreso, Long> {

    /**
     * Busca congresos activos
     */
    List<Congreso> findByActivoTrue();

    /**
     * Busca congresos por ciudad
     */
    List<Congreso> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);

    /**
     * Busca congresos por temática
     */
    List<Congreso> findByTematicaContainingIgnoreCaseAndActivoTrue(String tematica);

    /**
     * Busca congresos por rango de fechas
     */
    @Query("SELECT c FROM Congreso c WHERE c.activo = true AND c.fechaInicio >= :fechaInicio AND c.fechaFin <= :fechaFin")
    List<Congreso> findByFechaRange(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca congresos por nombre
     */
    List<Congreso> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    /**
     * Busca congresos por organizador
     */
    List<Congreso> findByOrganizador_IdUsuario(Long idOrganizador);
}
