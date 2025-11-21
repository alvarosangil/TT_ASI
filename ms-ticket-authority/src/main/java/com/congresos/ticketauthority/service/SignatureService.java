package com.congresos.ticketauthority.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para firmar y verificar tickets usando JWT
 */
@Service
public class SignatureService {

    @Value("${ticket.signature.secret}")
    private String secretKey;

    /**
     * Genera una firma para un ticket
     */
    public String generateSignature(String ticketId, String eventId, LocalDateTime expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ticketId", ticketId);
        claims.put("eventId", eventId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica si una firma es válida
     */
    public boolean isSignatureValid(String signature) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(signature);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el ticketId de la firma
     */
    public String extractTicketId(String signature) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(signature)
                .getBody();
        return claims.get("ticketId", String.class);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
