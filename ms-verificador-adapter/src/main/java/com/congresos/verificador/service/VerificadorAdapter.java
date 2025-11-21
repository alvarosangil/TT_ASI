package com.congresos.verificador.service;

import com.congresos.verificador.dto.VerifyTicketRequest;
import com.congresos.verificador.dto.VerifyTicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Servicio Adaptador para verificación de tickets
 * Implementa lógica de reintentos, caché y auditoría
 * CU9: Escaneo y control de acceso
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerificadorAdapter {

    private final TicketAuthorityClient ticketAuthorityClient;

    /**
     * Verifica un ticket QR
     * Incluye lógica de reintentos en caso de fallo temporal
     */
    public Mono<VerifyTicketResponse> verifyTicket(String qrPayload, String signature, String gateId) {
        log.info("Iniciando verificación de ticket - Gate: {}", gateId);
        
        VerifyTicketRequest request = new VerifyTicketRequest(qrPayload, signature, gateId);
        
        return ticketAuthorityClient.verifyTicket(request)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(retrySignal -> 
                            log.warn("Reintentando verificación - Intento {}", retrySignal.totalRetries() + 1)))
                .doOnSuccess(response -> 
                    log.info("Verificación completada - Status: {}, TicketId: {}", 
                            response.getStatus(), response.getTicketId()))
                .doOnError(error -> 
                    log.error("Error en verificación tras reintentos: {}", error.getMessage()));
    }
}
