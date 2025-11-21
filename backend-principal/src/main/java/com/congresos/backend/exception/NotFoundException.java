package com.congresos.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
