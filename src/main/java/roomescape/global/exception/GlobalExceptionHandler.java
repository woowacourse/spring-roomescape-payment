package roomescape.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.global.exception.authentication.AuthenticationException;
import roomescape.global.exception.payment.PaymentException;
import roomescape.global.exception.roomescape.RoomEscapeException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomEscapeException.class)
    public ResponseEntity<ErrorResponse> handleRoomEscapeException(final RoomEscapeException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getMessage()));
    }
}
