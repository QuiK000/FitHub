package com.dev.quikkkk.exception;

import com.dev.quikkkk.dto.response.ErrorResponse;
import com.dev.quikkkk.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();

        log.info("Business Exception: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity.status(
                ex.getErrorCode().getStatus() != null
                        ? ex.getErrorCode().getStatus()
                        : BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.ValidationError.builder()
                        .field(err.getField())
                        .code("INVALID_FIELD")
                        .message(err.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code(ErrorCode.VALIDATION_ERROR.getCode())
                        .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage())
                        .validationErrors(errors)
                        .build()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity violation";
        String code = "DATA_INTEGRITY_ERROR";
        String exMessage = ex.getMessage();

        if (exMessage != null) {
            if (exMessage.contains("unique constraint") || exMessage.contains("duplicate key")) {
                message = "A record with this value already exists";
                code = "DUPLICATE_ENTRY";
            } else if (exMessage.contains("foreign key constraint")) {
                message = "Cannot perform operation due to related accords";
                code = "FOREIGN_KEY_ERROR";
            } else if (exMessage.contains("not-null constraint")) {
                message = "Required field cannot be null";
                code = "NULL_VALUE_ERROR";
            }
        }

        log.error("Data integrity violation: {}", ex.getMessage());
        log.debug("Full exception: ", ex);

        return ResponseEntity.status(CONFLICT).body(
                ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(
                ErrorResponse.builder()
                        .code("ACCESS_DENIED")
                        .message("You don't have permission to access this resource")
                        .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ignoredEx) {
        log.warn("Bad credentials attempt");
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .code("INVALID_CREDENTIALS")
                        .message("Invalid email or password")
                        .build()
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ignoredEx) {
        log.warn("Disabled account login attempt");
        return ResponseEntity.status(FORBIDDEN).body(
                ErrorResponse.builder()
                        .code("ACCOUNT_DISABLED")
                        .message("Your account is disabled. Please verify your email")
                        .build()
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(LockedException ignoredEx) {
        log.warn("Locked account login attempt");
        return ResponseEntity.status(FORBIDDEN).body(
                ErrorResponse.builder()
                        .code("ACCOUNT_LOCKED")
                        .message("Your account has been locked. Please contact support")
                        .build()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("MISSING_PARAMETER")
                        .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        log.warn("Type mismatch: {}", message);
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("TYPE_MISMATCH")
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        log.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(NOT_FOUND).body(
                ErrorResponse.builder()
                        .code("ENDPOINT_NOT_FOUND")
                        .message("The requested endpoint does not exist")
                        .build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("INVALID_ARGUMENT")
                        .message(ex.getMessage() != null ? ex.getMessage() : "Invalid argument provider")
                        .build()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("ILLEGAL_STATE")
                        .message("The application is in an invalid state. Please try again")
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        String message = "An unexpected error occurred. Please try again later";

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(InvalidEmailAddressException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmailAddress(InvalidEmailAddressException ex) {
        log.warn("Invalid email address: {}", ex.getInvalidEmail());
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid email address");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("invalidEmail", ex.getInvalidEmail());

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Map<String, Object>> handleEmailSendException(EmailSendException ex) {
        log.error("Email send failed: recipient={}, type={}", ex.getRecipientEmail(), ex.getEmailType());
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Email send failed");
        errorResponse.put("message", "Failed to send email. Please try again later.");

        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<Map<String, Object>> handleEmailException(EmailException ex) {
        log.error("Email error: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Email error");
        errorResponse.put("message", "An error occurred while processing email.");

        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
