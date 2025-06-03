package roomescape.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.common.exception.custom.AlreadyInUseException;
import roomescape.common.exception.custom.AuthenticationException;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.common.exception.custom.LoginFailException;
import roomescape.common.exception.custom.PaymentClientException;
import roomescape.common.exception.dto.ErrorResponse;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
    }

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<ErrorResponse> handlePaymentBadRequestException(final PaymentClientException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("PAYMENT_BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ENTITY_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(AlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyUseException(final AlreadyInUseException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("ALREADY_IN_USE", e.getMessage()));
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<ErrorResponse> handleLoginFailException(final LoginFailException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("LOGIN_FAIL", e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("AUTHENTICATION", e.getMessage()));
    }
}