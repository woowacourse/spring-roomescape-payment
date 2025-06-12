package roomescape.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.auth.exception.TokenCreationException;
import roomescape.auth.exception.UnauthorizedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<Object> handlePaymentClientException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.BAD_REQUEST, e.getMessage(), null,
                null, request);

        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ReservationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequestException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.BAD_REQUEST, e.getMessage(), null,
                null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.NOT_FOUND, e.getMessage(), null,
                null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.UNAUTHORIZED, e.getMessage(), null,
                null, request);
        log.error(e.getMessage(), e);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.FORBIDDEN, e.getMessage(), null,
                null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(TokenCreationException.class)
    public ResponseEntity<Object> handleTokenCreationException(final Exception e, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                null, null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({Exception.class, RoomescapeException.class})
    public ResponseEntity<Object> handleInternalServerException(final Exception e, final WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        log.error("[예상치 못한 오류] URI: {}, IP: {}, 예외 유형: {}, 메시지: {}",
                servletRequest.getRequestURI(),
                servletRequest.getRemoteAddr(),
                e.getClass().getName(),
                e.getMessage(),
                e);

        ProblemDetail body = super.createProblemDetail(e, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.",
                null, null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
