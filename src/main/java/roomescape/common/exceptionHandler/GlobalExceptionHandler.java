package roomescape.common.exceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import roomescape.common.exception.PaymentException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.common.exceptionHandler.dto.ExceptionResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String EXCEPTION_PREFIX = "[ERROR] ";

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentException(
            final PaymentException e, final HttpServletRequest request) {
        return ResponseEntity.status(e.getStatusCode().value())
                .body(new ExceptionResponse(EXCEPTION_PREFIX + e.getMessage(), request.getRequestURI()));
    }

    /**
     * 400 Bad Request
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse notReadable(
            final HttpMessageNotReadableException exception, final HttpServletRequest request
    ) {
        Throwable rootCause = exception.getRootCause();
        if (rootCause instanceof IllegalArgumentException) {
            return new ExceptionResponse(EXCEPTION_PREFIX + rootCause.getMessage(), request.getRequestURI());
        }

        return new ExceptionResponse(EXCEPTION_PREFIX + "요청 형식이 올바르지 않습니다.", request.getRequestURI());
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleUnauthorizedException(
            final UnauthorizedException exception, final HttpServletRequest request
    ) {
        return new ExceptionResponse(EXCEPTION_PREFIX + exception.getMessage(), request.getRequestURI());
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleIllegalArgumentException(
            final IllegalArgumentException exception, final HttpServletRequest request
    ) {
        return new ExceptionResponse(EXCEPTION_PREFIX + exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse serverError(
            final NullPointerException exception, final HttpServletRequest request) {
        return new ExceptionResponse(EXCEPTION_PREFIX + exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse unknownException(final HttpServletRequest request) {
        return new ExceptionResponse(EXCEPTION_PREFIX + "예상치 못한 서버 오류입니다. 서버에 문의해주세요.", request.getRequestURI());
    }
}
