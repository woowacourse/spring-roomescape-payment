package roomescape.global.exception;

import io.jsonwebtoken.JwtException;
import java.net.SocketTimeoutException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.infrastructure.PaymentException;
import roomescape.payment.presentation.dto.PaymentErrorResponse;

@RestControllerAdvice
public class ExceptionController {

    private static final String PREFIX = "[ERROR] ";
    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentErrorResponse> handlePaymentException(PaymentException e) {
        return ResponseEntity.status(e.getCode()).body(new PaymentErrorResponse(
                e.getCode().toString(),
                e.getMessage(),
                e.getOrderId()
        ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<PaymentErrorResponse> handleResourceAccessException(ResourceAccessException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new PaymentErrorResponse(
                HttpStatus.GATEWAY_TIMEOUT.toString(),
                e.getMessage(),
                null
        ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(PREFIX + e.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException() {
        return ResponseEntity.badRequest().body(PREFIX + "시간 형식이 잘못되었습니다.");
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class, NoSuchElementException.class,
            DateTimeException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(PREFIX + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(PREFIX + e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.badRequest().body(PREFIX + "예상하지 못한 예외가 발생하였습니다. 상세 정보: " + e.getMessage());
    }
}
