package com.ehtisham.splitsync.infrastructure.exception;

import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(
            InvalidRequestException ex,
            HttpServletRequest request
    ) {
        var locale = LocaleContextHolder.getLocale();
        String path = request != null ? request.getRequestURI() : null;
        Map<String, String> fieldErrors = ex.getFieldErrors();
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            List<ApiResponse.ValidationError> errors = fieldErrors.entrySet().stream()
                    .map(entry -> ApiResponse.ValidationError.builder()
                            .field(entry.getKey())
                            .message(resolveMessage(entry.getValue(), locale))
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.status(ex.getHttpStatus())
                    .body(ApiResponse.error(resolveMessage(ex.getMessage(), locale), errors, path));
        }
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(resolveMessage(ex.getMessage(), locale), path));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var locale = LocaleContextHolder.getLocale();
        String path = request != null ? request.getRequestURI() : null;
        List<ApiResponse.ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> toValidationError(error, locale))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(resolveMessage("validation.failed", locale), errors, path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        var locale = LocaleContextHolder.getLocale();
        String path = request != null ? request.getRequestURI() : null;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(resolveMessage("error.unexpected", locale), path));
    }

    private ApiResponse.ValidationError toValidationError(FieldError error, java.util.Locale locale) {
        return ApiResponse.ValidationError.builder()
                .field(error.getField())
                .message(messageSource.getMessage(error, locale))
                .rejectedValue(error.getRejectedValue())
                .build();
    }

    private String resolveMessage(String code, java.util.Locale locale) {
        if (code == null || code.isBlank()) {
            return code;
        }
        return messageSource.getMessage(code, null, code, locale);
    }
}
