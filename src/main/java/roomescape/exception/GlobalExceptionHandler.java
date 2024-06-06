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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("[Request Error] " + e.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> httpClientErrorException(HttpClientErrorException e) {
        logger.info(e.getMessage(), e);
        ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
        if (errorResponse == null) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ErrorResponse("[Client Error] 관리자에게 문의하세요"));
        }
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("[Client Error] " + errorResponse.message()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> externalApiException(ExternalApiException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("[External Error] " + e.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("[Authorization Error] " + e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("[Data Access Error] " + e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("[Server Error] " + e.getMessage()));
    }
}