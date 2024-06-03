package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentException(PaymentException e) {
        log.warn("[PaymentException]", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toExceptionResponse());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        log.warn("[BadRequestException]", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getExceptionResponse());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        log.warn("[NotFoundException]", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("[AuthenticationException]", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getExceptionResponse());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException e) {
        log.warn("[AuthorizationException]", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getExceptionResponse());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("[MethodArgumentNotValidException]", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException e) {
        log.warn("[HandlerMethodValidationException]", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("[Exception]", e);
        return ResponseEntity.internalServerError()
                .body(ExceptionResponse.of(ErrorType.UNEXPECTED_SERVER_ERROR));
    }
}
