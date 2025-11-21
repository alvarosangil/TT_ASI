package com.congresos.verificador.controller;

import com.congresos.verificador.dto.VerifyTicketRequest;
import com.congresos.verificador.dto.VerifyTicketResponse;
import com.congresos.verificador.service.VerificadorAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para el servicio de verificación
 * Expone el endpoint para que el Backend Principal verifique tickets
 */
@RestController
@RequestMapping("/api/verificador")
@RequiredArgsConstructor
@Slf4j
public class VerificadorController {

    private final VerificadorAdapter verificadorAdapter;

    /**
     * Endpoint para verificar un ticket QR
     * POST /api/verificador/verifyTicket
     */
    @PostMapping("/verifyTicket")
    public Mono<ResponseEntity<VerifyTicketResponse>> verifyTicket(@RequestBody VerifyTicketRequest request) {
        log.info("Recibida solicitud de verificación - Gate: {}", request.getGateId());
        
        return verificadorAdapter.verifyTicket(
                        request.getQrPayload(),
                        request.getSignature(),
                        request.getGateId()
                )
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MS Verificador Adapter is running");
    }
}
