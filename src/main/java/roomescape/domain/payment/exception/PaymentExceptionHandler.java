package roomescape.domain.payment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.ErrorResponse;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class PaymentExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePaymentCredentialMissMatchException(final PaymentCredentialMissMatchException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePaymentConfirmClientFailException(final PaymentConfirmClientFailException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePaymentConfirmServerFailException(final PaymentConfirmServerFailException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
}
