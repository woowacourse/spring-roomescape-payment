package roomescape.web.controller.handler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.service.exception.PastReservationException;
import roomescape.service.exception.PaymentException;
import roomescape.service.exception.ReservationExistsException;
import roomescape.service.exception.RoomEscapeException;
import roomescape.web.exception.AuthorizationException;

@RestControllerAdvice
public class ControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(value = {
            PastReservationException.class,
            ReservationExistsException.class,
            IllegalArgumentException.class,
            NoSuchElementException.class,
            IllegalStateException.class,
    })
    public ResponseEntity<String> handleServiceException(RuntimeException e) {
        logger.error(e.getMessage(), e);

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleAuthException(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(e.getMessage(), e);

        BindingResult result = e.getBindingResult();
        String errMessage = result.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        return ResponseEntity.badRequest().body(errMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerException(Exception e) {
        logger.error(e.getMessage(), e);

        return ResponseEntity.internalServerError().body("예기치 못한 에러 발생   " + e.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<RoomEscapeException> handleHttpClientErrorException(PaymentException e) {
        logger.error(e.getPaymentErrorResponse().toString());
        logger.error(e.getMessage(), e);
        RoomEscapeException roomEscapeException = new RoomEscapeException(e.getMessage());
        return ResponseEntity.status(e.getStatusCode())
                .body(roomEscapeException);
    }
}
