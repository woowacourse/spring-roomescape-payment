package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.common.dto.ExceptionResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(
            final BusinessException exception, final HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(
            final IllegalArgumentException exception, final HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException exception, final HttpServletRequest request) {
        Throwable rootCause = exception.getRootCause();

        if (rootCause instanceof IllegalArgumentException) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, rootCause.getMessage(), request);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "요청 입력이 잘못되었습니다.", request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAccess(
            final ResourceAccessException exception, final HttpServletRequest request) {

        Throwable rootCause = exception.getRootCause();
        String message;

        if (rootCause instanceof SocketTimeoutException) {
            message = "서버 응답 시간이 초과되었습니다. (Read Timeout)";
        } else if (rootCause instanceof ConnectException) {
            message = "서버에 연결할 수 없습니다. (Connect Timeout)";
        } else {
            message = "외부 서버 접근 중 오류가 발생했습니다.";
        }

        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnexpectedException(
            final Exception exception, final HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류가 발생했습니다.", request);
    }

    private ResponseEntity<ExceptionResponse> buildErrorResponse(
            HttpStatus status, String message, HttpServletRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                status.value(), "[ERROR] " + message, request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }
}
