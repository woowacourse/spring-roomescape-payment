package roomescape.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import jakarta.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ExceptionResponse handleException(Exception e, HttpServletRequest request) {
        handleLoggingError(request, e, INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류 발생");
        return new ExceptionResponse(INTERNAL_SERVER_ERROR.value(), "서버 에러입니다", LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String exceptionMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("\n"));

        handleLoggingWarn(request, e, BAD_REQUEST.value(), exceptionMessage);
        return new ExceptionResponse(BAD_REQUEST.value(), exceptionMessage, LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
            HttpServletRequest request) {
        Throwable rootCause = e.getRootCause();
        String exceptionMessage = "잘못된 형식의 값이 입력되었습니다.";
        if (rootCause instanceof DateTimeException) {
            exceptionMessage = "잘못된 날짜 또는 시간 형식입니다.";
        }

        handleLoggingWarn(request, e, BAD_REQUEST.value(), exceptionMessage);
        return new ExceptionResponse(BAD_REQUEST.value(), exceptionMessage, LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ExceptionResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        String exceptionMessage = "잘못된 형식의 값이 입력되었습니다.";

        handleLoggingWarn(request, e, BAD_REQUEST.value(), exceptionMessage);
        return new ExceptionResponse(BAD_REQUEST.value(), exceptionMessage, LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ExceptionResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
            HttpServletRequest request) {
        String exceptionMessage = "필수 정보(파라미터)가 누락되었습니다";

        handleLoggingWarn(request, e, BAD_REQUEST.value(), exceptionMessage);
        return new ExceptionResponse(BAD_REQUEST.value(), exceptionMessage, LocalDateTime.now());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionResponse handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        handleLoggingWarn(request, e, BAD_REQUEST.value(), e.getMessage());
        return new ExceptionResponse(BAD_REQUEST.value(), e.getMessage(), LocalDateTime.now());
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ExceptionResponse handleBusinessRuleViolationException(BusinessRuleViolationException e,
            HttpServletRequest request) {

        handleLoggingWarn(request, e, UNPROCESSABLE_ENTITY.value(), e.getMessage());
        return new ExceptionResponse(UNPROCESSABLE_ENTITY.value(), e.getMessage(), LocalDateTime.now());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionResponse handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {

        handleLoggingWarn(request, e, NOT_FOUND.value(), e.getMessage());
        return new ExceptionResponse(NOT_FOUND.value(), e.getMessage(), LocalDateTime.now());
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ResourceInUseException.class)
    public ExceptionResponse handleResourceInUseException(ResourceInUseException e, HttpServletRequest request) {
        handleLoggingWarn(request, e, CONFLICT.value(), e.getMessage());
        return new ExceptionResponse(CONFLICT.value(), e.getMessage(), LocalDateTime.now());
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ExceptionResponse handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        handleLoggingWarn(request, e, UNAUTHORIZED.value(), e.getMessage());
        return new ExceptionResponse(UNAUTHORIZED.value(), e.getMessage(), LocalDateTime.now());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AuthorizationException.class)
    public ExceptionResponse handleAuthorizationException(AuthorizationException e, HttpServletRequest request) {
        handleLoggingWarn(request, e, FORBIDDEN.value(), e.getMessage());
        return new ExceptionResponse(FORBIDDEN.value(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ClientFailException.class)
    public ResponseEntity<ExceptionResponse> handleClientFailException(ClientFailException e,
            HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                e.getStatusCode(),
                e.getMessage(),
                LocalDateTime.now()
        );
        handleLoggingWarn(request, e, e.getStatusCode(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(exceptionResponse);
    }

    @ResponseStatus(GATEWAY_TIMEOUT)
    @ExceptionHandler(ClientTimeoutException.class)
    public ExceptionResponse handleClientTimeoutException(ClientTimeoutException e, HttpServletRequest request) {
        handleLoggingWarn(request, e, GATEWAY_TIMEOUT.value(), e.getMessage());
        return new ExceptionResponse(GATEWAY_TIMEOUT.value(), e.getMessage(), LocalDateTime.now());
    }

    private void handleLoggingWarn(HttpServletRequest request, Exception ex, int statusCode, String message) {
        String requestURI = request.getRequestURI();
        log.warn("[Client EXCEPTION] URL = {} | Status = {} | Exception = {} \n Message = {}",
                requestURI, statusCode, ex.getClass().getName(), message, ex);
    }

    private void handleLoggingError(HttpServletRequest request, Exception ex, int statusCode, String message) {
        String requestURI = request.getRequestURI();
        log.error("[SERVER EXCEPTION] URL = {} | Status = {} | Exception = {} \\n Message = {}\"",
                requestURI, statusCode, ex.getClass().getName(), message, ex);
    }
}
