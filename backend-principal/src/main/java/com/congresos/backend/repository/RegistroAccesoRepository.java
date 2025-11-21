package com.congresos.backend.repository;

import com.congresos.backend.model.domain.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad RegistroAcceso
 * CU9: Escaneo y control de acceso
 * CU21: Ver Estadísticas
 */
@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    /**
     * Busca registros de acceso de un congreso
     */
    List<RegistroAcceso> findByCongreso_IdCongresoOrderByFechaHoraEscaneoDesc(Long idCongreso);

    /**
     * Busca registros de acceso de una sesión
     */
    List<RegistroAcceso> findBySesion_IdSesionOrderByFechaHoraEscaneoDesc(Long idSesion);

    /**
     * Cuenta accesos válidos a un congreso
     */
    @Query("SELECT COUNT(r) FROM RegistroAcceso r WHERE r.congreso.idCongreso = :idCongreso AND r.resultado = 'VALIDO'")
    Long countAccesosValidosCongreso(@Param("idCongreso") Long idCongreso);

    /**
     * Cuenta accesos válidos a una sesión
     */
    @Query("SELECT COUNT(r) FROM RegistroAcceso r WHERE r.sesion.idSesion = :idSesion AND r.resultado = 'VALIDO'")
    Long countAccesosValidosSesion(@Param("idSesion") Long idSesion);
}
