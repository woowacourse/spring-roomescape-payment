package roomescape.domain.payment.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.domain.payment.exception.PaymentProcessException;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentProcessException.class)
    public ResponseEntity<String> handlePaymentException(final PaymentProcessException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}
