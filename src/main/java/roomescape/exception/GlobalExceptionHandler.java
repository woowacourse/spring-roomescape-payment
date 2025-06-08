package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.auth.AuthTokenNotFoundException;
import roomescape.exception.auth.AuthenticationException;
import roomescape.exception.auth.AuthorizationException;
import roomescape.exception.payment.PaymentException;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.exception.resource.ResourceNotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Object> handlePaymentException(final PaymentException e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());

        return ResponseEntity.status(e.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(final ResourceNotFoundException e) {
        final HttpStatus responseStatus = HttpStatus.NOT_FOUND;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExistException(final AlreadyExistException e) {
        final HttpStatus responseStatus = HttpStatus.CONFLICT;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(AuthTokenNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAuthTokenNotFoundException(final AuthTokenNotFoundException e) {
        final HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(final AuthenticationException e) {
        final HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ProblemDetail> handleAuthorizationException(final AuthorizationException e) {
        final HttpStatus responseStatus = HttpStatus.FORBIDDEN;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(final IllegalArgumentException e) {
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(final Exception e) {
        log.error(e.getMessage());
        final HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(responseStatus, e.getMessage());

        return ResponseEntity.status(responseStatus)
                .body(problemDetail);
    }
}
