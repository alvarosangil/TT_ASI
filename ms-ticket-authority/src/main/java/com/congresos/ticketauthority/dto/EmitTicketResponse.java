package com.congresos.ticketauthority.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de emisión de ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmitTicketResponse {
    private String ticketId;
    private String qrPayload;
    private String signature;
}
