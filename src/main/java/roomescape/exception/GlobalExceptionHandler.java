package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("[Request Error] " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("[Request Error] " + e.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> httpClientErrorException(HttpClientErrorException e) {
        ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
        if (errorResponse == null) {
            log.warn("[Client Error]", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ErrorResponse("[Client Error] 관리자에게 문의하세요"));
        }
        log.info("[Client Error] " + errorResponse.message());
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("[Client Error] " + errorResponse.message()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> externalApiException(ExternalApiException e) {
        log.info("[External Error] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("[External Error] " + e.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException e) {
        log.info("[Authorization Error] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("[Authorization Error] " + e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("[Data Access Error] " + e.getMessage(),e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("[Data Access Error] 관리자에게 문의하세요"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Server Error] " + e.getMessage(),e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("[Server Error] 관리자에게 문의하세요"));
    }
}
