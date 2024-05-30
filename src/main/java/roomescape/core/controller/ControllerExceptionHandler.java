package roomescape.core.controller;

import io.jsonwebtoken.JwtException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.core.dto.exception.ExceptionResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentServerException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    public static final String REQUEST_TIMEOUT_EXCEPTION_MESSAGE = "요청 시간이 만료되었습니다. 다시 요청해주세요.";

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {
        final ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getBindingResult()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            final IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJwtException(final JwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleDuplicateKeyException(
            final DuplicateKeyException exception) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handlePaymentException(final PaymentException exception) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleResourceAccessException(
            final ResourceAccessException exception) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.REQUEST_TIMEOUT,
                        REQUEST_TIMEOUT_EXCEPTION_MESSAGE));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handlePaymentServerException(
            final PaymentServerException exception) {
        return ResponseEntity.internalServerError()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleRuntimeException(final RuntimeException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.internalServerError()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                        "서버에 문제가 있습니다. 잠시 후 다시 시도해주세요."));
    }
}
