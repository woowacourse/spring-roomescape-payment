package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.auth.AuthenticationException;
import roomescape.exception.auth.AuthorizationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RootBusinessException.class)
    public ResponseEntity<ErrorResponse> handle(RootBusinessException e) {
        log.warn("RootException: message={}", e.getMessage());
        return ErrorResponse.plainResponse(HttpStatus.BAD_REQUEST, e.code()).toResponseEntity();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handle(AuthenticationException e) {
        log.warn("AuthenticatedException: message={}", e.detailMessage());
        return ErrorResponse.securedResponse(HttpStatus.UNAUTHORIZED, e.clientMessage()).toResponseEntity();
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handle(AuthorizationException e) {
        log.warn("AuthorizationException: message={}", e.detailMessage());
        return ErrorResponse.securedResponse(HttpStatus.FORBIDDEN, e.clientMessage()).toResponseEntity();
    }

    @ExceptionHandler(ExternalApiErrorException.class)
    public ResponseEntity<ErrorResponse> handle(ExternalApiErrorException e) {
        log.warn("ExternalApiErrorException: message={}", e.getMessage());
        return ErrorResponse.externalApiErrorResponse().toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Exception: message={}", e.getMessage(), e);
        return ErrorResponse.securedResponse().toResponseEntity();
    }
}
