package com.cesde.arteestilo.exception;

public class PedidoNoCancelableException extends RuntimeException {
    public PedidoNoCancelableException(String mensaje) {
        super(mensaje);
    }
}