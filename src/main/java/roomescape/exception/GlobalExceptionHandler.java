package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import roomescape.dto.ErrorResponseBody;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorResponseBody> handleRoomescapeException(RoomescapeException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatusCode())
                .body(new ErrorResponseBody(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseBody> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponseBody("[Server Error] " + e.getMessage()));
    }
}
