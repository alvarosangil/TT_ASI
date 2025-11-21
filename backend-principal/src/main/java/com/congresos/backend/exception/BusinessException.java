package com.congresos.backend.exception;

/**
 * Excepción lanzada cuando hay un conflicto de negocio
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
