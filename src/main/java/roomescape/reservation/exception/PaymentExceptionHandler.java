package roomescape.reservation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<String> handlePaymentClientException(PaymentClientException e) {
        return ResponseEntity.status(e.getHttpStatusCode()).body(e.getMessage());
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<String> handlePaymentServerException(PaymentServerException e) {
        return ResponseEntity.status(e.getHttpStatusCode()).body(e.getMessage());
    }
}
