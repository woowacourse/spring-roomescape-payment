package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ExceptionResponse> handle(RoomescapeException ex) {
        log.warn("Roomescape exception [status={},errorCode={},message={}]", ex.getStatusCode(), ex.getErrorCode(), ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ExceptionResponse(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("Server Exception [message={}]",e.getMessage(),e);
        return ResponseEntity.internalServerError()
                .body(ExceptionResponse.of(ErrorType.UNEXPECTED_SERVER_ERROR));
    }
}
