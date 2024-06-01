package roomescape.domain.reservation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.ErrorResponse;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ReservationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleInvalidReserveInputException(final InvalidReserveInputException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleInvalidReservationWaitInputException(final InvalidReservationWaitInputException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
}
