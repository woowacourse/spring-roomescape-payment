package roomescape.exception.common;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = RoomescapeException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(RoomescapeException exception) {
        logError(exception);
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), exception.getStatus());
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> handleJsonParsingException(HttpMessageConversionException exception) {
        logError(exception);
        return new ResponseEntity<>(new ErrorResponse("요청 body에 유효하지 않은 필드가 존재합니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleRequestValidateException(Exception exception) {
        logError(exception);
        return new ResponseEntity<>(new ErrorResponse("요청 null 또는 유효하지 않은 값이 존재합니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleRequestException(Exception exception) {
        logError(exception);
        return new ResponseEntity<>(new ErrorResponse("요청 경로에 필요한 변수가 제공되지 않았습니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        logError(exception);
        return new ResponseEntity<>(new ErrorResponse("서버 에러입니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception exception) {
        String exceptionMessage = exception.getMessage();
        if (!StringUtils.isBlank(exceptionMessage)) {
            LOGGER.error("{}\n\t{}", exceptionMessage, exception.getStackTrace()[0]);
            return;
        }
        LOGGER.error(exceptionMessage);
    }
}
