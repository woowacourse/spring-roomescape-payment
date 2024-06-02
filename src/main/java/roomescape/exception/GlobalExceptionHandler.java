package roomescape.exception;

import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<String> roomescapeExceptionHandler(RoomescapeException exception) {
        logError(exception);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> dateTimeParseExceptionHandler(DateTimeParseException exception) {
        logError(exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("올바르지 않은 시간/날짜 형식입니다.");
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentExceptionResponse> exceptionHandler(PaymentException exception) {
        logError(exception);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getPaymentExceptionResponse());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionHandler(RuntimeException exception) {
        logError(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부에서 에러가 발생했습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        logError(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부에서 에러가 발생했습니다.");
    }
}
