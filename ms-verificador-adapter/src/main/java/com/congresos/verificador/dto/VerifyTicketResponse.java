package com.congresos.verificador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de verificación de ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTicketResponse {
    private String status;
    private String ticketId;
    private Boolean usedNow;
    private String verificationId;
    private LocalDateTime lastUseAt;
}
