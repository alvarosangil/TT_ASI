package com.congresos.verificador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de verificación de ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTicketRequest {
    private String qrPayload;
    private String signature;
    private String gateId;
}
