package com.congresos.ticketauthority.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo en memoria para representar un ticket
 * En un entorno real, esto se almacenaría en una base de datos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String ticketId;
    private String eventId;
    private String holderName;
    private String holderDoc;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime createdAt;
    private List<TicketUsage> usages = new ArrayList<>();
    private String signature;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(validTo);
    }

    public boolean isNotYetValid() {
        return LocalDateTime.now().isBefore(validFrom);
    }

    public boolean hasBeenUsed() {
        return !usages.isEmpty();
    }

    public void addUsage(String gateId, String verificationId) {
        usages.add(new TicketUsage(
                verificationId,
                gateId,
                LocalDateTime.now()
        ));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketUsage {
        private String verificationId;
        private String gateId;
        private LocalDateTime usedAt;
    }
}
