package roomescape.global.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.payment.error.exception.PaymentClientException;
import roomescape.payment.error.exception.PaymentServerException;
import roomescape.reservation.dto.response.ErrorResponse;

@Slf4j
@RestControllerAdvice(basePackages = "roomescape.payment")
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<ErrorResponse> handlePaymentClientException(PaymentClientException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(e.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ErrorResponse> handlePaymentServerException(PaymentServerException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(e.getHttpStatusCode()).body(response);
    }
}
