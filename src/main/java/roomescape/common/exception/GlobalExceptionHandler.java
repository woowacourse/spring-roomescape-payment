package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.common.exception.error.ErrorCode;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.common.exception.error.GeneralErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(final PaymentException e,
                                                                final HttpServletRequest request) {
        log.warn("External API Payment Failed - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(e, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ErrorResponse> handlePaymentServerException(final PaymentServerException e,
                                                                final HttpServletRequest request) {
        log.error("External API Payment Server Error - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(e, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConnectTimeOutException.class)
    public ResponseEntity<ErrorResponse> handleConnectTimeOutException(final ConnectTimeOutException e,
                                                                       final HttpServletRequest request) {
        log.warn("Connect Time Out - URI '{} {}', {} ", request.getMethod(), request.getRequestURI(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e,
                                                               final HttpServletRequest request) {
        log.warn("{} - URI '{} {}', {} ", e.getClass().getSimpleName(), request.getMethod(), request.getRequestURI(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final CustomException e,
                                                               final HttpServletRequest request) {
        log.warn("Unexpected Custom Error - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(final DateTimeParseException e,
                                                                      final HttpServletRequest request) {
        log.error("Datetime Parsing Failed - URI '{} {}'", request.getMethod(), request.getRequestURI(), e);
        ErrorResponse errorResponse = ErrorResponse.of(GeneralErrorCode.INVALID_DATETIME_FORMAT, request);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllException(final HttpServletRequest request,
                                                            final RuntimeException e) {
        log.error("Unexpected Error - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        ErrorResponse errorResponse = ErrorResponse.of(GeneralErrorCode.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
