package roomescape.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.ErrorResponse;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class AuthExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleLogInException(final LogInException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleAccessUnauthorizedException(final UnauthorizedException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(FORBIDDEN)
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
}
