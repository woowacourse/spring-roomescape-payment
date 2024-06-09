package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.dto.ErrorResponse;

import java.time.DateTimeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        String message = exception.getMessage();
        logger.warn("message: {}", message, exception.getCause());
        ErrorResponse data = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        String message = exception.getMessage();
        logger.warn("message: {}", message, exception.getCause());
        ErrorResponse data = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        logger.warn("message: {}", exception.getMessage(), exception.getCause());

        if (exception.getRootCause() instanceof DateTimeException) {
            return handleDateTimeParseException();
        }

        ErrorResponse data = new ErrorResponse("요청에 잘못된 형식의 값이 있습니다.");
        return ResponseEntity.badRequest().body(data);
    }

    private ResponseEntity<ErrorResponse> handleDateTimeParseException() {
        ErrorResponse data = new ErrorResponse("잘못된 형식의 날짜 혹은 시간입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        logger.warn("message: {}", exception.getMessage(), exception.getCause());
        ErrorResponse data = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception) {
        String message = exception.getMessage();
        logger.warn("message: {}", exception.getMessage(), exception.getCause());
        ErrorResponse data = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
        String message = exception.getMessage();
        logger.warn("message: {}", exception.getMessage(), exception.getCause());
        ErrorResponse data = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        logger.warn("message: {}", exception.getMessage(), exception.getCause());
        ErrorResponse data = new ErrorResponse("모든 파라미터를 입력해야 합니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception exception) {
        logger.error("message: {}", exception.getMessage(), exception.getCause());
        ErrorResponse data = new ErrorResponse("서버에 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }
}
