package roomescape.core.controller;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.core.dto.exception.ExceptionResponse;
import roomescape.core.dto.exception.HttpExceptionResponse;
import roomescape.core.exception.PaymentException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    public static final String REQUEST_TIMEOUT_EXCEPTION_MESSAGE = "요청 시간이 만료되었습니다. 다시 요청해주세요.";

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {
        final ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getBindingResult()
        );

        logger.error("Invalid method arguments. Exception status code: {}, message: {}, ", exception.getStatusCode(),
                exception.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(final IllegalArgumentException exception) {
        logger.error("Bad request. message: {}, ", exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJwtException(final JwtException exception) {
        logger.error("Invalid Jwt. message: {}, ", exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleDuplicateKeyException(final DuplicateKeyException exception) {
        logger.error("Key duplicated. message: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handlePaymentException(final PaymentException exception) {
        final HttpExceptionResponse responseBody = exception.getResponseBody();

        logger.error("Payment failed. message: {}", responseBody.getMessage());
        return ResponseEntity.status(exception.getStatusCode())
                .body(ProblemDetail.forStatusAndDetail(exception.getStatusCode(), responseBody.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleResourceAccessException(final ResourceAccessException exception) {
        logger.error("Cannot access external API resources. message: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.REQUEST_TIMEOUT, REQUEST_TIMEOUT_EXCEPTION_MESSAGE));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleRuntimeException(final RuntimeException exception) {
        logger.error("Unexpected exception. message: {}", exception.getMessage());

        return ResponseEntity.internalServerError()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }
}
