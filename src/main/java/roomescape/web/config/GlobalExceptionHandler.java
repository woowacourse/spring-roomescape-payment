package roomescape.web.config;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.CustomException;
import roomescape.exception.ErrorResult;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_PREFIX = "exception occur : {}";

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResult> handleCustomException(CustomException exception) {
        return new ResponseEntity<>(new ErrorResult(exception.getMessage()), exception.getStatus());
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResult> handleValidationException(BindException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(new ErrorResult(message), BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<ErrorResult> handleJsonParsingException(HttpMessageConversionException exception) {
        return new ResponseEntity<>(new ErrorResult("유효하지 않은 필드가 존재합니다."), BAD_REQUEST);
    }

    @ExceptionHandler(value = JwtException.class)
    public ProblemDetail handleJwtException(JwtException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception exception) {
        log.error(ERROR_PREFIX, exception);
        return new ResponseEntity<>(new ErrorResult("서버 에러입니다."), INTERNAL_SERVER_ERROR);
    }
}
