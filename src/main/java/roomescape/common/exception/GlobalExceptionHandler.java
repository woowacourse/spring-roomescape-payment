package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import roomescape.common.dto.response.ErrorResponse;
import roomescape.exception.auth.ForbiddenException;
import roomescape.exception.auth.UnauthorizedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            final BusinessException e, final HttpServletRequest request) {
        log.warn("[{} {}] → BusinessException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(
            final DateTimeParseException e, final HttpServletRequest request) {
        log.warn("[{} {}] → DateTimeParseException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            final IllegalArgumentException e, final HttpServletRequest request) {
        log.warn("[{} {}] → IllegalArgumentException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            final UnauthorizedException e, final HttpServletRequest request) {
        log.warn("[{} {}] → UnauthorizedException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            final ForbiddenException e, final HttpServletRequest request) {
        log.warn("[{} {}] → ForbiddenException: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            final MethodArgumentNotValidException e, final HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("[{} {}] → Validation error: {}", request.getMethod(), request.getRequestURI(), message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message.isBlank() ? "요청 데이터에 오류가 있습니다." : message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBodyFormat(
            final HttpMessageNotReadableException e, final HttpServletRequest request) {
        String message = extractRootCauseMessage(e, "요청 본문 형식이 올바르지 않습니다.");
        log.warn("[{} {}] → MessageNotReadableException: {}", request.getMethod(), request.getRequestURI(), message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleExternalConnectionIssue(
            final ResourceAccessException e, final HttpServletRequest request) {
        String message = extractRootCauseMessage(e, "외부 서버 접근 중 오류가 발생했습니다.");
        log.error("[{} {}] → ResourceAccessException: {}", request.getMethod(), request.getRequestURI(), message, e);
        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, message, request);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(
            final RestClientException e, final HttpServletRequest request) {
        log.error("[{} {}] → RestClientException: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "결제 서버와의 통신 중 오류가 발생했습니다.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            final Exception e, final HttpServletRequest request) {
        log.error("[{} {}] → Unhandled exception: {} - {}", request.getMethod(), request.getRequestURI(),
                e.getClass().getSimpleName(), e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류가 발생했습니다.", request);
    }

    private String extractRootCauseMessage(Throwable throwable, String fallbackMessage) {
        if (throwable == null) {
            return fallbackMessage;
        }
        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;

        if (cause instanceof IllegalArgumentException) {
            return cause.getMessage();
        } else if (cause instanceof SocketTimeoutException) {
            return "서버 응답 시간이 초과되었습니다. (Read Timeout)";
        } else if (cause instanceof ConnectException) {
            return "서버에 연결할 수 없습니다. (Connect Timeout)";
        }
        return fallbackMessage;
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            final HttpStatus status,
            final String message,
            final HttpServletRequest request
    ) {
        log.warn("Handled [{}] at [{}]: {}", status, request.getRequestURI(), message);
        final ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                formatErrorMessage(message),
                extractPath(request)
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    private String formatErrorMessage(final String message) {
        return String.format("[ERROR] %s", message);
    }

    private String extractPath(final HttpServletRequest request) {
        String path = request.getServletPath();
        if (request.getQueryString() != null) {
            path += "?" + request.getQueryString();
        }
        return path;
    }
}
