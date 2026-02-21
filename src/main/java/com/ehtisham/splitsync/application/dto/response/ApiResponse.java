package com.ehtisham.splitsync.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic API Response wrapper
 * Use this for all API responses to maintain consistency
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    public enum Status {
        SUCCESS,
        VALIDATION_ERROR,
        ERROR
    }

    private boolean success;
    private Status status;
    private String message;
    private T data;
    private List<ValidationError> errors;
    private LocalDateTime timestamp;
    private String path;

    // ============================================
    // Success Response Builders
    // ============================================

    /**
     * Success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(Status.SUCCESS)
                .message("Request processed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response with data and custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(Status.SUCCESS)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(Status.SUCCESS)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ============================================
    // Error Response Builders
    // ============================================

    /**
     * Error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(Status.ERROR)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Error response with message and path
     */
    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(Status.ERROR)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Error response with validation errors
     */
    public static <T> ApiResponse<T> error(String message, List<ValidationError> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(Status.VALIDATION_ERROR)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Error response with validation errors and path
     */
    public static <T> ApiResponse<T> error(String message, List<ValidationError> errors, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(Status.VALIDATION_ERROR)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ============================================
    // Validation Error Inner Class
    // ============================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}

