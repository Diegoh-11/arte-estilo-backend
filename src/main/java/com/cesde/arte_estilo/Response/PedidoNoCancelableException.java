package com.cesde.arte_estilo.Response;

public class PedidoNoCancelableException extends RuntimeException {
    public PedidoNoCancelableException(String mensaje) {
        super(mensaje);
    }
}