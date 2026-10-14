package com.cesde.arte_estilo.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO estándar devuelto por el GlobalExceptionHandler.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime fecha;
    private String message;
    private int codigoHttp;
}