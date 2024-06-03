package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.exception.customexception.api.AbstractApiException;
import roomescape.exception.customexception.api.ApiBadRequestException;
import roomescape.exception.customexception.api.ApiException;
import roomescape.exception.customexception.api.ApiTimeOutException;
import roomescape.exception.customexception.business.AbstractBusinessException;
import roomescape.exception.customexception.business.RoomEscapeBusinessException;
import roomescape.exception.customexception.security.AbstractSecurityException;
import roomescape.exception.customexception.security.AuthenticationException;
import roomescape.exception.customexception.security.AuthorizationException;
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

    @ExceptionHandler(ApiBadRequestException.class)
    public ResponseEntity<Object> handleApiBadRequestException(ApiException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(ErrorCode.INVALID_PARAMETER, e.getMessage());
    }

    @ExceptionHandler(ApiTimeOutException.class)
    public ResponseEntity<Object> handleApiTimeoutException(ApiException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(ErrorCode.INTERNAL_SERVER, e.getMessage());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e) {
        logger.error(e.getMessage(), e);
        return makeErrorResponseEntity(ErrorCode.INTERNAL_SERVER, e.getMessage());
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

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<Object> handleApiException(AbstractApiException e) {
        return makeErrorResponseEntity(ErrorCode.INTERNAL_SERVER);
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

    private String resolveMethodArgumentNotValidMessage(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(error -> error.getField() + "는 " + error.getDefaultMessage())
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
