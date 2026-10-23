package com.cesde.arteestilo.exception;

/** Conflicto con el estado actual de los datos (duplicados, dependencias). Responde HTTP 409. */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
