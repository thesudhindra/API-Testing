package com.playground.common.web;

import com.playground.common.api.ProblemTypes;
import com.playground.common.exception.PlaygroundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlaygroundException.class)
    public ResponseEntity<ProblemDetail> handlePlaygroundException(PlaygroundException ex) {
        ProblemDetail problem = ProblemDetailSupport.create(
                ex.getStatus(), ex.getType(), ex.getTitle(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetailSupport.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ProblemTypes.VALIDATION,
                "Validation Failed",
                "One or more fields failed validation");

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());
        problem.setProperty("errors", errors);
        return ResponseEntity.unprocessableEntity().body(problem);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NoResourceFoundException ex) {
        ProblemDetail problem = ProblemDetailSupport.create(
                HttpStatus.NOT_FOUND,
                ProblemTypes.NOT_FOUND,
                "Not Found",
                "The requested resource was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetailSupport.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemTypes.INTERNAL,
                "Internal Server Error",
                "An unexpected error occurred");
        return ResponseEntity.internalServerError().body(problem);
    }

    private Map<String, String> toFieldError(FieldError fieldError) {
        return Map.of(
                "field", fieldError.getField(),
                "message", fieldError.getDefaultMessage() == null ? "invalid" : fieldError.getDefaultMessage()
        );
    }

    @RestControllerAdvice
    @ConditionalOnClass(AccessDeniedException.class)
    static class SecurityAdvice {

        @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
        public ResponseEntity<ProblemDetail> handleAccessDenied(Exception ex) {
            ProblemDetail problem = ProblemDetailSupport.create(
                    HttpStatus.FORBIDDEN,
                    ProblemTypes.FORBIDDEN,
                    "Forbidden",
                    ex.getMessage() == null ? "Access is denied" : ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
        }
    }
}
