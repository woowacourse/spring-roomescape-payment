package roomescape.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.common.exception.custom.AlreadyInUseException;
import roomescape.common.exception.custom.AuthenticationException;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.common.exception.custom.LoginFailException;
import roomescape.common.exception.custom.PaymentClientException;
import roomescape.common.exception.custom.PaymentServerException;
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
                .body(new ErrorResponse("PAYMENT_CLIENT_ERROR", e.getMessage()));
    }

    @ExceptionHandler(PaymentBadRequestException.class)
    public ResponseEntity<PaymentBadRequestException> handlePaymentBadRequestException(PaymentBadRequestException exception) {
        return ResponseEntity.badRequest()
                .body(exception);
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

    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleInvalidFormat(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_FORMAT", "형식이 올바르지 않습니다." + e.getMessage()));
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ErrorResponse> handlePaymentServerException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("PAYMENT_SERVER_ERROR", "결제를 처리할 수 없습니다. 잠시 후 다시 시도해주세요."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}