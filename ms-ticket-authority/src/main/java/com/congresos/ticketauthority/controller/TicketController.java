package com.congresos.ticketauthority.controller;

import com.congresos.ticketauthority.dto.*;
import com.congresos.ticketauthority.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para el servicio de Ticket Authority
 * Expone la API según la especificación
 */
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * POST /api/v1/tickets - Emitir un nuevo ticket
     */
    @PostMapping
    public ResponseEntity<EmitTicketResponse> emitTicket(@RequestBody EmitTicketRequest request) {
        log.info("Solicitud de emisión de ticket para evento: {}", request.getEventId());
        EmitTicketResponse response = ticketService.emitTicket(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/tickets/verify - Verificar un ticket
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyTicketResponse> verifyTicket(@RequestBody VerifyTicketRequest request) {
        log.info("Solicitud de verificación de ticket en gate: {}", request.getGateId());
        VerifyTicketResponse response = ticketService.verifyTicket(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/tickets/{ticketId}/status - Consultar estado de un ticket
     */
    @GetMapping("/{ticketId}/status")
    public ResponseEntity<Map<String, Object>> getTicketStatus(@PathVariable String ticketId) {
        log.info("Consulta de estado del ticket: {}", ticketId);
        Map<String, Object> status = ticketService.getTicketStatus(ticketId);
        return ResponseEntity.ok(status);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MS Ticket Authority is running");
    }
}
