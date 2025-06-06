package roomescape.reservation.error.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.reservation.dto.response.ErrorResponse;
import roomescape.reservation.error.exception.PaymentClientException;
import roomescape.reservation.error.exception.PaymentServerException;

@RestControllerAdvice
@Order(1)
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
