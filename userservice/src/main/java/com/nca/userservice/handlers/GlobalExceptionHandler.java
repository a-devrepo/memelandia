package com.nca.userservice.handlers;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectProvider<Tracer> tracerProvider;

    public GlobalExceptionHandler(ObjectProvider<Tracer> tracerProvider) {
        this.tracerProvider = tracerProvider;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Erro de validação nos campos informados.");
        problemDetail.setTitle("Campos Inválidos");
        problemDetail.setType(URI.create("https://nca.com.br/errors/validation"));

        // Tratando chaves duplicadas (ex: @NotBlank e @Size no mesmo campo)
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msgExistente, msgNova) -> msgExistente + " | " + msgNova
                ));

        problemDetail.setProperty("fields", errors);
        addObservabilityMetadata(problemDetail);

        return createResponseEntity(problemDetail, headers, status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Regra de Negócio Violada");
        addObservabilityMetadata(problemDetail);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado."
        );
        problemDetail.setTitle("Erro de Servidor");
        addObservabilityMetadata(problemDetail);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private void addObservabilityMetadata(ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now());

        // Recupera o tracer de forma segura
        tracerProvider.ifAvailable(tracer -> {
            var currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                problemDetail.setProperty("traceId", currentSpan.context().traceId());
            }
        });
    }
}