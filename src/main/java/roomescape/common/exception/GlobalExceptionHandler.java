package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(
            final HttpServletRequest request,
            final DateTimeParseException ex
    ) {
        logException(request, ex);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_DATETIME_FORMAT, request);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(
            final HttpServletRequest request,
            final PaymentException ex
    ) {
        logException(request, ex);
        final ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().value(), ex.getCode(), ex.getMessage(), request.getMethod(), request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            final HttpServletRequest request,
            final CustomException ex
    ) {
        logException(request, ex);
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllException(
            final HttpServletRequest request,
            final Exception ex
    ) {
        logException(request, ex);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    private void logException(
            final HttpServletRequest request,
            final Exception ex
    ) {
        log.warn("[EXCEPTION] {} {} {} {}",
                request.getMethod(),
                getFullRequestUri(request),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

    private String getFullRequestUri(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final String query = request.getQueryString();

        if (query != null) {
            return uri + "?" + query;
        }
        return uri;
    }
}
