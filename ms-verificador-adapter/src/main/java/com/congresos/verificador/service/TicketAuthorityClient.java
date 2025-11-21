package com.congresos.verificador.service;

import com.congresos.verificador.dto.VerifyTicketRequest;
import com.congresos.verificador.dto.VerifyTicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Cliente HTTP para comunicarse con el servicio Ticket Authority
 */
@Service
@Slf4j
public class TicketAuthorityClient {

    private final WebClient webClient;

    public TicketAuthorityClient(@Value("${ticket.authority.url}") String ticketAuthorityUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(ticketAuthorityUrl)
                .build();
    }

    /**
     * Llama al servicio externo para verificar un ticket
     */
    public Mono<VerifyTicketResponse> verifyTicket(VerifyTicketRequest request) {
        log.info("Verificando ticket en servicio externo - Gate: {}", request.getGateId());
        
        return webClient.post()
                .uri("/api/v1/tickets/verify")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(VerifyTicketResponse.class)
                .doOnSuccess(response -> log.info("Verificación exitosa - Status: {}", response.getStatus()))
                .doOnError(error -> log.error("Error al verificar ticket: {}", error.getMessage()));
    }
}
