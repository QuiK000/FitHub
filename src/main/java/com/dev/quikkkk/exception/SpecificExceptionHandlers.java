package com.dev.quikkkk.exception;

import com.dev.quikkkk.dto.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CONTENT_TOO_LARGE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
@Slf4j
public class SpecificExceptionHandlers {
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking conflict: {}", ex.getMessage());
        return ResponseEntity.status(CONFLICT).body(
                ErrorResponse.builder()
                        .code("OPTIMISTIC_LOCK_ERROR")
                        .message("The resource was modified by another user. Please refresh and try again.")
                        .build()
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleObjectOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        log.warn("Object optimistic locking failure: {}", ex.getMessage());
        return ResponseEntity.status(CONFLICT).body(
                ErrorResponse.builder()
                        .code("CONCURRENT_MODIFICATION")
                        .message("Resource has been modified. Please reload and retry.")
                        .build()
        );
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> handleTransaction(TransactionException ex) {
        log.warn("Transaction error: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("TRANSACTION_ERROR")
                        .message("Transaction failed. Please try again.")
                        .build()
        );
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException ex) {
        log.error("SQL error: {}", ex.getMessage(), ex);

        String message = "Database error occurred";
        String code = "DATABASE_ERROR";

        if (ex.getSQLState() != null) {
            code = switch (ex.getSQLState()) {
                case "23505" -> {
                    message = "Duplicate entry detected";
                    yield "DUPLICATE_KEY";
                }
                case "23503" -> {
                    message = "Cannot perform operation due to existing references";
                    yield "FOREIGN_KEY_VIOLATION";
                }
                case "23502" -> {
                    message = "Required field is missing";
                    yield "NOIT_NULL_VIOLATION";
                }
                case "08001" -> {
                    message = "Database connection error";
                    yield "DB_CONNECTION_ERROR";
                }
                default -> code;
            };
        }

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex) {
        log.error("Data access error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("DATA_ACCESS_ERROR")
                        .message("Failed to access data. Please try again.")
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        String message = "Invalid request format";

        if (ex.getMessage().contains("JSON parse error")) {
            message = "Invalid JSON format in request body";
        } else if (ex.getMessage().contains("Required request body is missing")) {
            message = "Request body is required";
        }

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("MALFORMED_REQUEST")
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP method not supported: {}", ex.getMethod());
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(
                ErrorResponse.builder()
                        .code("METHOD_NOT_ALLOWED")
                        .message(String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()))
                        .build()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("Media type not supported: {}", ex.getContentType());
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE).body(
                ErrorResponse.builder()
                        .code("UNSUPPORTED_MEDIA_TYPE")
                        .message("Content type '" + ex.getContentType() + "' is not supported.")
                        .build()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("File upload size exceeded: {}", ex.getMessage());
        return ResponseEntity.status(CONTENT_TOO_LARGE).body(
                ErrorResponse.builder()
                        .code("FILE_TOO_LARGE")
                        .message("File size exceeds maximum allowed limit")
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .code("AUTHENTICATION_FAILED")
                        .message("Authentication failed. Please check your credentials.")
                        .build()
        );
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeout(TimeoutException ex) {
        log.error("Request timeout: {}", ex.getMessage());
        return ResponseEntity.status(REQUEST_TIMEOUT).body(
                ErrorResponse.builder()
                        .code("REQUEST_TIMEOUT")
                        .message("Request timed out. Please try again.")
                        .build()
        );
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleAsyncTimeout(AsyncRequestTimeoutException ignoredEx) {
        log.error("Async request timeout");
        return ResponseEntity.status(REQUEST_TIMEOUT).body(
                ErrorResponse.builder()
                        .code("ASYNC_TIMEOUT")
                        .message("Request processing took too long")
                        .build()
        );
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessing(JsonProcessingException ex) {
        log.error("JSON processing error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("JSON_PROCESSING_ERROR")
                        .message("Invalid JSON format: " + ex.getOriginalMessage())
                        .build()
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(NullPointerException ex) {
        log.error("Null pointer exception", ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("NULL_POINTER_ERROR")
                        .message("An unexpected error occurred. Our team has been notified.")
                        .build()
        );
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnection(RedisConnectionFailureException ex) {
        log.error("Redis connection failure: {}", ex.getMessage());
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(
                ErrorResponse.builder()
                        .code("CACHE_UNAVAILABLE")
                        .message("Cache service temporarily unavailable")
                        .build()
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ignoredEx) {
        log.warn("JWT token expired");
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .code("TOKEN_EXPIRED")
                        .message("Your session has expired. Please login again.")
                        .build()
        );
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ignoredEx) {
        log.warn("Malformed JWT token");
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .code("INVALID_TOKEN")
                        .message("Invalid authentication token")
                        .build()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(
                ErrorResponse.builder()
                        .code("RESOURCE_NOT_FOUND")
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(CONFLICT).body(
                ErrorResponse.builder()
                        .code("DUPLICATE_RESOURCE")
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        return ResponseEntity.status(PAYMENT_REQUIRED).body(
                ErrorResponse.builder()
                        .code("INSUFFICIENT_BALANCE")
                        .message(ex.getMessage())
                        .build()
        );
    }
}
