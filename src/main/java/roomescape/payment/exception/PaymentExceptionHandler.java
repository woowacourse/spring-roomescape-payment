package roomescape.payment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.exception.ErrorType;
import roomescape.exception.ExceptionResponse;

@ControllerAdvice
public class PaymentExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentException(PaymentException e) {
        log.warn(e.getMessage());
        if (PaymentServerExceptionCode.isServerError(e.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ExceptionResponse.of(ErrorType.PAYMENT_SERVER_FAILED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }
}
