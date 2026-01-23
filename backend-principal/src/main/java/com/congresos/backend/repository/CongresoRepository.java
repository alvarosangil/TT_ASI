package com.congresos.backend.repository;

import com.congresos.backend.model.domain.Congreso;
import com.congresos.backend.model.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    /**
     * CU12: Búsqueda por texto (nombre, descripción, lugar)
     */
    @Query("SELECT c FROM Congreso c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(c.lugar) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<Congreso> buscarPorTexto(@Param("texto") String texto);

    /**
     * CU12: Filtrar por ciudad y temática
     */
    @Query("SELECT c FROM Congreso c WHERE c.activo = true AND " +
           "(:ciudad IS NULL OR LOWER(c.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) AND " +
           "(:tematica IS NULL OR LOWER(c.tematica) LIKE LOWER(CONCAT('%', :tematica, '%')))")
    List<Congreso> filtrarPorCiudadYTematica(@Param("ciudad") String ciudad, @Param("tematica") String tematica);

    /**
     * CU3: Obtener congreso con sesiones
     */
    @Query("SELECT c FROM Congreso c LEFT JOIN FETCH c.sesiones WHERE c.idCongreso = :id")
    Optional<Congreso> findByIdWithSesiones(@Param("id") Long id);

    /**
     * CU23/CU24: Obtener congresos por organizador
     */
    List<Congreso> findByOrganizadorOrderByFechaCreacionDesc(Usuario organizador);

    /**
     * Ordenar congresos activos por fecha de inicio descendente
     */
    List<Congreso> findByActivoTrueOrderByFechaInicioDesc();
}
