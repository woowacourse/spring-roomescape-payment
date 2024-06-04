package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.exception.customexception.*;
import roomescape.exception.dto.ErrorResponse;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> authenticationException(AuthenticationException e) {
        return makeErrorResponseEntity(ErrorCode.UNAUTHROZIED);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleAuthorizationException() {
        return makeErrorResponseEntity(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(RoomEscapeBusinessException.class)
    public ResponseEntity<Object> handleIllegalArgument(RoomEscapeBusinessException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(ErrorCode.INVALID_PARAMETER, e.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Object> handlePaymentException(PaymentException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(e.getHttpStatus(), e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @Nullable MethodArgumentNotValidException exception,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatusCode status,
            @Nullable WebRequest request) {

        if (exception == null) {
            return makeErrorResponseEntity(ErrorCode.INVALID_PARAMETER);
        }

        return makeErrorResponseEntity(
                ErrorCode.INVALID_PARAMETER,
                resolveMethodArgumentNotValidMessage(exception)
        );
    }

    @ExceptionHandler(AbstractBusinessException.class)
    public ResponseEntity<Object> handleBusinessException(AbstractBusinessException e) {
        return makeErrorResponseEntity(ErrorCode.INTERNAL_SERVER);
    }

    @ExceptionHandler(AbstractSecurityException.class)
    public ResponseEntity<Object> handleBusinessException(AbstractSecurityException e) {
        return makeErrorResponseEntity(ErrorCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(ErrorCode.INTERNAL_SERVER, e.getMessage());
    }

    private ResponseEntity<Object> makeErrorResponseEntity(ErrorCode code) {
        ErrorResponse errorResponse = new ErrorResponse(code);
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    private ResponseEntity<Object> makeErrorResponseEntity(ErrorCode code, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse(code, errorMessage);
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    private ResponseEntity<Object> makeErrorResponseEntity(HttpStatus httpStatus, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus, errorMessage);
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    private String resolveMethodArgumentNotValidMessage(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(error -> error.getField() + "는 " + error.getDefaultMessage())
                .collect(Collectors.joining(System.lineSeparator()));
    }
}