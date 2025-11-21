package com.congresos.ticketauthority.service;

import com.congresos.ticketauthority.dto.*;
import com.congresos.ticketauthority.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio principal para gestión de tickets
 * Implementa emisión y verificación de tickets QR
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final SignatureService signatureService;
    
    // Almacenamiento en memoria (en producción sería una base de datos)
    private final Map<String, Ticket> ticketStore = new ConcurrentHashMap<>();

    /**
     * Emite un nuevo ticket
     */
    public EmitTicketResponse emitTicket(EmitTicketRequest request) {
        // Generar ID único para el ticket
        String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Crear el ticket
        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketId);
        ticket.setEventId(request.getEventId());
        ticket.setHolderName(request.getHolderName());
        ticket.setHolderDoc(request.getHolderDoc());
        ticket.setValidFrom(request.getValidFrom());
        ticket.setValidTo(request.getValidTo());
        ticket.setCreatedAt(LocalDateTime.now());
        
        // Generar firma
        String signature = signatureService.generateSignature(
                ticketId,
                request.getEventId(),
                request.getValidTo()
        );
        ticket.setSignature(signature);
        
        // Almacenar el ticket
        ticketStore.put(ticketId, ticket);
        
        // Generar payload del QR (simplificado, en producción sería más complejo)
        String qrPayload = Base64.getEncoder().encodeToString(
                (ticketId + ":" + request.getEventId()).getBytes()
        );
        
        log.info("Ticket emitido: {} para evento: {}", ticketId, request.getEventId());
        
        return new EmitTicketResponse(ticketId, qrPayload, signature);
    }

    /**
     * Verifica un ticket
     */
    public VerifyTicketResponse verifyTicket(VerifyTicketRequest request) {
        log.info("Verificando ticket en gate: {}", request.getGateId());
        
        // Verificar la firma
        if (!signatureService.isSignatureValid(request.getSignature())) {
            log.warn("Firma inválida o manipulada");
            return new VerifyTicketResponse("TAMPERED", null, false, null, null);
        }
        
        // Extraer el ticketId
        String ticketId = signatureService.extractTicketId(request.getSignature());
        
        // Buscar el ticket
        Ticket ticket = ticketStore.get(ticketId);
        if (ticket == null) {
            log.warn("Ticket no encontrado: {}", ticketId);
            return new VerifyTicketResponse("TAMPERED", null, false, null, null);
        }
        
        // Verificar si está caducado
        if (ticket.isExpired()) {
            log.warn("Ticket caducado: {}", ticketId);
            return new VerifyTicketResponse("EXPIRED", ticketId, false, null, null);
        }
        
        // Verificar si aún no es válido
        if (ticket.isNotYetValid()) {
            log.warn("Ticket aún no válido: {}", ticketId);
            return new VerifyTicketResponse("NOT_YET_VALID", ticketId, false, null, null);
        }
        
        // Verificar si ya fue usado
        if (ticket.hasBeenUsed()) {
            LocalDateTime lastUse = ticket.getUsages().get(ticket.getUsages().size() - 1).getUsedAt();
            log.warn("Ticket ya usado: {}", ticketId);
            return new VerifyTicketResponse("ALREADY_USED", ticketId, false, null, lastUse);
        }
        
        // Marcar como usado
        String verificationId = "VER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ticket.addUsage(request.getGateId(), verificationId);
        
        log.info("Ticket verificado exitosamente: {}", ticketId);
        return new VerifyTicketResponse("VALID", ticketId, true, verificationId, null);
    }

    /**
     * Consulta el estado de un ticket
     */
    public Map<String, Object> getTicketStatus(String ticketId) {
        Ticket ticket = ticketStore.get(ticketId);
        
        if (ticket == null) {
            return Map.of("error", "Ticket no encontrado");
        }
        
        String status = ticket.hasBeenUsed() ? "USED" : "UNUSED";
        LocalDateTime firstUse = ticket.hasBeenUsed() 
                ? ticket.getUsages().get(0).getUsedAt() 
                : null;
        
        Map<String, Object> response = new HashMap<>();
        response.put("ticketId", ticketId);
        response.put("status", status);
        response.put("firstUseAt", firstUse);
        response.put("uses", ticket.getUsages().size());
        
        return response;
    }
}
