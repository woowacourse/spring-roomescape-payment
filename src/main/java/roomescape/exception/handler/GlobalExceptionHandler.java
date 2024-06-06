package roomescape.exception.handler;

import io.jsonwebtoken.JwtException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.dto.response.ExceptionInfo;
import roomescape.exception.PaymentException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class, BadRequestException.class})
    public ResponseEntity<String> handleBadRequestException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionInfo> handlePaymentException(PaymentException ex) {
        logger.error(ex.getMessage(), ex);
        ExceptionInfo paymentException = new ExceptionInfo(ex.getStatusCode().toString(), ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(paymentException);
    }
}
