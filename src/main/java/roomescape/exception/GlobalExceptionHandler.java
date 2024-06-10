package roomescape.exception;

import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.RoomescapeApplication;
import roomescape.dto.response.reservation.PaymentExceptionResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(RoomescapeApplication.class);

    @ExceptionHandler
    public ResponseEntity<RoomescapeExceptionResponse> roomescapeExceptionHandler(RoomescapeException exception) {
        logError(exception);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getRoomescapeExceptionResponse());
    }

    @ExceptionHandler
    public ResponseEntity<String> dateTimeParseExceptionHandler(DateTimeParseException exception) {
        logError(exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("올바르지 않은 시간/날짜 형식입니다.");
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentExceptionResponse> paymentExceptionHandler(PaymentException exception) {
        logError(exception);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getTossExceptionResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        logError(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부에서 에러가 발생했습니다.");
    }

    private void logError(Exception exception) {
        log.error("Error occur {}", exception.toString());
    }
}
