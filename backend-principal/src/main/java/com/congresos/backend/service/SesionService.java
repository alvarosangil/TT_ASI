package com.congresos.backend.service;

import com.congresos.backend.dto.SesionDTO;
import com.congresos.backend.dto.SesionRequest;
import com.congresos.backend.exception.BusinessException;
import com.congresos.backend.exception.NotFoundException;
import com.congresos.backend.model.domain.Congreso;
import com.congresos.backend.model.domain.Sesion;
import com.congresos.backend.repository.CongresoRepository;
import com.congresos.backend.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para Sesiones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SesionService {

    private final SesionRepository sesionRepository;
    private final CongresoRepository congresoRepository;

    /**
     * CU4: Obtener sesiones de un congreso (público)
     */
    @Transactional(readOnly = true)
    public List<SesionDTO> obtenerSesionesPorCongreso(Long idCongreso) {
        Congreso congreso = congresoRepository.findById(idCongreso)
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado con ID: " + idCongreso));

        return congreso.getSesiones().stream()
                .filter(Sesion::getActiva)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener detalle de una sesión
     */
    @Transactional(readOnly = true)
    public SesionDTO obtenerSesionPorId(Long id) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sesión no encontrada con ID: " + id));

        if (!sesion.getActiva()) {
            throw new BusinessException("La sesión no está disponible");
        }

        return convertirADTO(sesion);
    }

    /**
     * CU26: Crear sesión (solo ORGANIZADOR propietario del congreso)
     */
    @Transactional
    public SesionDTO crearSesion(SesionRequest request, String emailOrganizador) {
        log.info("Creando sesión: {} por organizador: {}", request.getTitulo(), emailOrganizador);

        // Buscar congreso
        Congreso congreso = congresoRepository.findById(request.getIdCongreso())
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado"));

        // Verificar que sea el organizador propietario
        if (!congreso.getOrganizador().getEmail().equals(emailOrganizador)) {
            throw new BusinessException("No tienes permisos para crear sesiones en este congreso");
        }

        // Validaciones
        validarFechasSesion(request.getFechaHoraInicio(), request.getFechaHoraFin());
        validarSesionDentroDeCongreso(request.getFechaHoraInicio(), request.getFechaHoraFin(), 
                                      congreso.getFechaInicio(), congreso.getFechaFin());

        // Crear sesión
        Sesion sesion = new Sesion();
        sesion.setTitulo(request.getTitulo());
        sesion.setDescripcion(request.getDescripcion());
        sesion.setPonente(request.getPonente());
        sesion.setSala(request.getSala());
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setFechaHoraFin(request.getFechaHoraFin());
        sesion.setAforoMaximo(request.getAforoMaximo());
        sesion.setAforoActual(0);
        sesion.setCongreso(congreso);
        sesion.setActiva(true);

        Sesion sesionGuardada = sesionRepository.save(sesion);
        log.info("Sesión creada exitosamente con ID: {}", sesionGuardada.getIdSesion());

        return convertirADTO(sesionGuardada);
    }

    /**
     * CU27: Editar sesión (solo ORGANIZADOR propietario del congreso)
     */
    @Transactional
    public SesionDTO editarSesion(Long id, SesionRequest request, String emailOrganizador) {
        log.info("Editando sesión ID: {} por organizador: {}", id, emailOrganizador);

        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sesión no encontrada con ID: " + id));

        Congreso congreso = sesion.getCongreso();

        // Verificar que sea el organizador propietario
        if (!congreso.getOrganizador().getEmail().equals(emailOrganizador)) {
            throw new BusinessException("No tienes permisos para editar esta sesión");
        }

        // No permitir editar sesiones que ya iniciaron
        if (sesion.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede editar una sesión que ya inició");
        }

        // Validaciones
        validarFechasSesion(request.getFechaHoraInicio(), request.getFechaHoraFin());
        validarSesionDentroDeCongreso(request.getFechaHoraInicio(), request.getFechaHoraFin(),
                                      congreso.getFechaInicio(), congreso.getFechaFin());

        // Actualizar campos
        sesion.setTitulo(request.getTitulo());
        sesion.setDescripcion(request.getDescripcion());
        sesion.setPonente(request.getPonente());
        sesion.setSala(request.getSala());
        sesion.setFechaHoraInicio(request.getFechaHoraInicio());
        sesion.setFechaHoraFin(request.getFechaHoraFin());
        sesion.setAforoMaximo(request.getAforoMaximo());

        Sesion sesionActualizada = sesionRepository.save(sesion);
        log.info("Sesión actualizada exitosamente con ID: {}", sesionActualizada.getIdSesion());

        return convertirADTO(sesionActualizada);
    }

    /**
     * CU28: Eliminar sesión (solo ORGANIZADOR propietario del congreso)
     */
    @Transactional
    public void eliminarSesion(Long id, String emailOrganizador) {
        log.info("Eliminando sesión ID: {} por organizador: {}", id, emailOrganizador);

        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sesión no encontrada con ID: " + id));

        Congreso congreso = sesion.getCongreso();

        // Verificar que sea el organizador propietario
        if (!congreso.getOrganizador().getEmail().equals(emailOrganizador)) {
            throw new BusinessException("No tienes permisos para eliminar esta sesión");
        }

        // No permitir eliminar sesiones que ya iniciaron
        if (sesion.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede eliminar una sesión que ya inició");
        }

        // Verificar si hay inscripciones
        if (sesion.getAforoActual() > 0) {
            throw new BusinessException("No se puede eliminar una sesión con inscripciones. Aforo actual: " 
                                       + sesion.getAforoActual());
        }

        // Eliminación lógica (soft delete)
        sesion.setActiva(false);
        sesionRepository.save(sesion);

        log.info("Sesión eliminada exitosamente con ID: {}", id);
    }

    // ==================== VALIDACIONES ====================

    private void validarFechasSesion(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }

        if (inicio.isAfter(fin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (inicio.isBefore(LocalDateTime.now())) {
            throw new BusinessException("La fecha de inicio no puede ser anterior a la fecha/hora actual");
        }
    }

    private void validarSesionDentroDeCongreso(LocalDateTime inicioSesion, LocalDateTime finSesion,
                                               LocalDate inicioCongreso, LocalDate finCongreso) {
        LocalDate fechaSesion = inicioSesion.toLocalDate();
        
        if (fechaSesion.isBefore(inicioCongreso) || fechaSesion.isAfter(finCongreso)) {
            throw new BusinessException("La sesión debe estar dentro de las fechas del congreso");
        }
    }

    // ==================== CONVERSIONES DTO ====================

    private SesionDTO convertirADTO(Sesion sesion) {
        SesionDTO dto = new SesionDTO();
        dto.setIdSesion(sesion.getIdSesion());
        dto.setTitulo(sesion.getTitulo());
        dto.setDescripcion(sesion.getDescripcion());
        dto.setPonente(sesion.getPonente());
        dto.setSala(sesion.getSala());
        dto.setFechaHoraInicio(sesion.getFechaHoraInicio());
        dto.setFechaHoraFin(sesion.getFechaHoraFin());
        dto.setAforoMaximo(sesion.getAforoMaximo());
        dto.setAforoActual(sesion.getAforoActual());
        dto.setPlazasDisponibles(sesion.getAforoMaximo() - sesion.getAforoActual());
        return dto;
    }
}
