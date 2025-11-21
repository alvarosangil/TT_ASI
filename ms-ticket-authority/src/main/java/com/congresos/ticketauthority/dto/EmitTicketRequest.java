package com.congresos.ticketauthority.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para solicitud de emisión de ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmitTicketRequest {
    private String eventId;
    private String holderName;
    private String holderDoc;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
