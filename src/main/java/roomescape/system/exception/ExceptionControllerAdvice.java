package roomescape.system.exception;

import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.system.dto.response.ErrorResponse;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {RoomEscapeException.class})
    public ErrorResponse handleRoomEscapeException(RoomEscapeException e, HttpServletResponse response) {
        logger.error("{}{}", e.getMessage(), e.getInvalidValue().orElse(""), e);
        response.setStatus(e.getHttpStatus().value());
        return ErrorResponse.of(e.getErrorType(), e.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleResourceAccessException(ResourceAccessException e) {
        logger.error(e.getMessage(), e);
        return ErrorResponse.of(ErrorType.PAYMENT_SERVER_ERROR, ErrorType.PAYMENT_SERVER_ERROR.getDescription());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error(e.getMessage(), e);
        return ErrorResponse.of(ErrorType.INVALID_REQUEST_DATA_TYPE,
                ErrorType.INVALID_REQUEST_DATA_TYPE.getDescription());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String messages = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.error(messages, e);
        return ErrorResponse.of(ErrorType.INVALID_REQUEST_DATA, messages);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error(e.getMessage(), e);
        return ErrorResponse.of(ErrorType.METHOD_NOT_ALLOWED, ErrorType.METHOD_NOT_ALLOWED.getDescription());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ErrorResponse.of(ErrorType.INTERNAL_SERVER_ERROR, ErrorType.INTERNAL_SERVER_ERROR.getDescription());
    }
}
