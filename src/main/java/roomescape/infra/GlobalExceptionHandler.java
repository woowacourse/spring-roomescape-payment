package roomescape.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import roomescape.exception.PaymentException;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.global.GlobalExceptionCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RoomEscapeException.class)
    public ResponseEntity<String> handleCustomRoomEscapeException(RoomEscapeException exception) {
        logger.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(exception.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handleCustomPaymentException(PaymentException exception) {
        logger.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        logger.warn(exception.getMessage(), exception);
        return GlobalExceptionCode.METHOD_ARGUMENT_TYPE_INVALID.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(Exception exception) {
        logger.warn(exception.getMessage(), exception);
        return GlobalExceptionCode.INTERNAL_SERVER_ERROR.getMessage();
    }
}
