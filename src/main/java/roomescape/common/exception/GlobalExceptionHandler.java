package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(
            DateTimeParseException e,
            HttpServletRequest request
    ) {
        logExceptionToWarn(e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_DATETIME_FORMAT, request);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e, HttpServletRequest request) {
        logExceptionToWarn(e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getStatusCode().value(), e.getCode(), e.getMessage(),
                request.getMethod(), request.getRequestURI());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        logExceptionToWarn(e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllException(RuntimeException e, HttpServletRequest request) {
        logExceptionToWarn(e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e, HttpServletRequest request) {
        logExceptionToError(e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        logExceptionToError(e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    private void logExceptionToWarn(Exception e) {
        log.atWarn().log(e.getMessage(), e);
    }

    private void logExceptionToError(Exception e) {
        log.atError().log(e.getMessage(), e);
    }
}
