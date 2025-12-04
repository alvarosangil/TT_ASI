package com.congresos.backend.service;

import com.congresos.backend.dto.CongresoDTO;
import com.congresos.backend.dto.CongresoRequest;
import com.congresos.backend.dto.SesionDTO;
import com.congresos.backend.exception.BusinessException;
import com.congresos.backend.exception.NotFoundException;
import com.congresos.backend.model.domain.Congreso;
import com.congresos.backend.model.domain.Usuario;
import com.congresos.backend.repository.CongresoRepository;
import com.congresos.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para Congresos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CongresoService {

    private final CongresoRepository congresoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * CU12: Buscar y filtrar congresos
     */
    @Transactional(readOnly = true)
    public List<CongresoDTO> buscarCongresos(String query, String ciudad, String tematica) {
        List<Congreso> congresos;

        if (query != null && !query.isEmpty()) {
            congresos = congresoRepository.buscarPorTexto(query);
        } else if (ciudad != null || tematica != null) {
            congresos = congresoRepository.filtrarPorCiudadYTematica(ciudad, tematica);
        } else {
            congresos = congresoRepository.findByActivoTrueOrderByFechaInicioDesc();
        }

        return congresos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * CU19: Ver detalle de congreso
     */
    @Transactional(readOnly = true)
    public CongresoDTO obtenerDetalleCongreso(Long id) {
        Congreso congreso = congresoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado con ID: " + id));

        if (!congreso.getActivo()) {
            throw new BusinessException("El congreso no está disponible");
        }

        return convertirADTO(congreso);
    }

    /**
     * CU3: Ver programa completo del congreso
     */
    @Transactional(readOnly = true)
    public CongresoDTO obtenerProgramaCompleto(Long id) {
        Congreso congreso = congresoRepository.findByIdWithSesiones(id)
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado con ID: " + id));

        if (!congreso.getActivo()) {
            throw new BusinessException("El congreso no está disponible");
        }

        return convertirADTOConSesiones(congreso);
    }

    /**
     * CU23: Crear congreso (solo ORGANIZADOR)
     */
    @Transactional
    public CongresoDTO crearCongreso(CongresoRequest request, String emailOrganizador) {
        log.info("Creando congreso: {} por organizador: {}", request.getNombre(), emailOrganizador);

        // Buscar organizador
        Usuario organizador = usuarioRepository.findByEmail(emailOrganizador)
                .orElseThrow(() -> new NotFoundException("Organizador no encontrado"));

        if (organizador.getTipoUsuario() != Usuario.TipoUsuario.ORGANIZADOR) {
            throw new BusinessException("Solo los organizadores pueden crear congresos");
        }

        // Validaciones de negocio
        validarFechasCongreso(request.getFechaInicio(), request.getFechaFin());
        validarPrecio(request.getPrecio(), request.getEsPago());

        // Crear congreso
        Congreso congreso = new Congreso();
        congreso.setNombre(request.getNombre());
        congreso.setDescripcion(request.getDescripcion());
        congreso.setFechaInicio(request.getFechaInicio());
        congreso.setFechaFin(request.getFechaFin());
        congreso.setLugar(request.getLugar());
        congreso.setCiudad(request.getCiudad());
        congreso.setPrecio(request.getPrecio());
        congreso.setEsPago(request.getEsPago());
        congreso.setImagenPortada(request.getImagenPortada());
        congreso.setTematica(request.getTematica());
        congreso.setOrganizador(organizador);
        congreso.setActivo(true);

        Congreso congresoGuardado = congresoRepository.save(congreso);
        log.info("Congreso creado exitosamente con ID: {}", congresoGuardado.getIdCongreso());

        return convertirADTO(congresoGuardado);
    }

    /**
     * CU24: Editar congreso (solo ORGANIZADOR propietario)
     */
    @Transactional
    public CongresoDTO editarCongreso(Long id, CongresoRequest request, String emailOrganizador) {
        log.info("Editando congreso ID: {} por organizador: {}", id, emailOrganizador);

        Congreso congreso = congresoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado con ID: " + id));

        // Verificar que el organizador sea el propietario
        if (!congreso.getOrganizador().getEmail().equals(emailOrganizador)) {
            throw new BusinessException("No tienes permisos para editar este congreso");
        }

        // Validaciones
        validarFechasCongreso(request.getFechaInicio(), request.getFechaFin());
        validarPrecio(request.getPrecio(), request.getEsPago());

        // Actualizar campos
        congreso.setNombre(request.getNombre());
        congreso.setDescripcion(request.getDescripcion());
        congreso.setFechaInicio(request.getFechaInicio());
        congreso.setFechaFin(request.getFechaFin());
        congreso.setLugar(request.getLugar());
        congreso.setCiudad(request.getCiudad());
        congreso.setPrecio(request.getPrecio());
        congreso.setEsPago(request.getEsPago());
        congreso.setImagenPortada(request.getImagenPortada());
        congreso.setTematica(request.getTematica());

        Congreso congresoActualizado = congresoRepository.save(congreso);
        log.info("Congreso actualizado exitosamente con ID: {}", congresoActualizado.getIdCongreso());

        return convertirADTO(congresoActualizado);
    }

    /**
     * Obtener congresos del organizador
     */
    @Transactional(readOnly = true)
    public List<CongresoDTO> obtenerCongresosPorOrganizador(String emailOrganizador) {
        Usuario organizador = usuarioRepository.findByEmail(emailOrganizador)
                .orElseThrow(() -> new NotFoundException("Organizador no encontrado"));

        List<Congreso> congresos = congresoRepository.findByOrganizadorOrderByFechaCreacionDesc(organizador);

        return congresos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * CU25: Eliminar congreso (solo ORGANIZADOR propietario)
     */
    @Transactional
    public void eliminarCongreso(Long id, String emailOrganizador) {
        log.info("Eliminando congreso ID: {} por organizador: {}", id, emailOrganizador);

        Congreso congreso = congresoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Congreso no encontrado con ID: " + id));

        // Verificar que el organizador sea el propietario
        if (!congreso.getOrganizador().getEmail().equals(emailOrganizador)) {
            throw new BusinessException("No tienes permisos para eliminar este congreso");
        }

        // No permitir eliminar congresos que ya iniciaron
        if (congreso.getFechaInicio().isBefore(LocalDate.now())) {
            throw new BusinessException("No se puede eliminar un congreso que ya inició o finalizó");
        }

        // Eliminación lógica (soft delete)
        congreso.setActivo(false);
        congresoRepository.save(congreso);
        
        log.info("Congreso eliminado exitosamente con ID: {}", id);
    }

    // ==================== VALIDACIONES ====================

    private void validarFechasCongreso(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
    }

    private void validarPrecio(java.math.BigDecimal precio, Boolean esPago) {
        if (esPago != null && esPago) {
            if (precio == null || precio.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El precio debe ser mayor a 0 para congresos de pago");
            }
        }
    }

    // ==================== CONVERSIONES DTO ====================

    private CongresoDTO convertirADTO(Congreso congreso) {
        CongresoDTO dto = new CongresoDTO();
        dto.setIdCongreso(congreso.getIdCongreso());
        dto.setNombre(congreso.getNombre());
        dto.setDescripcion(congreso.getDescripcion());
        dto.setFechaInicio(congreso.getFechaInicio());
        dto.setFechaFin(congreso.getFechaFin());
        dto.setLugar(congreso.getLugar());
        dto.setCiudad(congreso.getCiudad());
        dto.setPrecio(congreso.getPrecio());
        dto.setEsPago(congreso.getEsPago());
        dto.setImagenPortada(congreso.getImagenPortada());
        dto.setTematica(congreso.getTematica());
        dto.setActivo(congreso.getActivo());
        dto.setNombreOrganizador(congreso.getOrganizador().getNombreCompleto());
        dto.setFechaCreacion(congreso.getFechaCreacion());
        return dto;
    }

    private CongresoDTO convertirADTOConSesiones(Congreso congreso) {
        CongresoDTO dto = convertirADTO(congreso);
        
        List<SesionDTO> sesiones = congreso.getSesiones().stream()
                .filter(sesion -> sesion.getActiva())
                .map(this::convertirSesionADTO)
                .collect(Collectors.toList());
        
        dto.setSesiones(sesiones);
        return dto;
    }

    private SesionDTO convertirSesionADTO(com.congresos.backend.model.domain.Sesion sesion) {
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
