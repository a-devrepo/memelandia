package com.nca.memeservice.handlers;

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

    @ExceptionHandler(RuntimeException.class) // Você pode criar uma exception personalizada se preferir
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Se a mensagem contiver "não encontrada", podemos subir para 404
        if (e.getMessage().toLowerCase().contains("não encontrada")) {
            status = HttpStatus.NOT_FOUND;
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setTitle("Erro na Operação");
        addObservabilityMetadata(problemDetail);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado no Meme Service."
        );
        problemDetail.setTitle("Erro de Servidor");
        addObservabilityMetadata(problemDetail);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private void addObservabilityMetadata(ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now());

        tracerProvider.ifAvailable(tracer -> {
            var currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                problemDetail.setProperty("traceId", currentSpan.context().traceId());
            }
        });
    }
}