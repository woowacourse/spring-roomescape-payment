package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.common.exception.impl.ConflictException;
import roomescape.common.exception.impl.ForbiddenException;
import roomescape.common.exception.impl.NotFoundException;
import roomescape.common.exception.impl.UnauthorizedException;
import roomescape.payment.application.PaymentException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(
            final Exception e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("error", e, handler, request, e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부에 오류가 발생했습니다.");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handle(
            final BadRequestException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "BadRequestException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handle(
            final NotFoundException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "NotFoundException");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handle(
            final ConflictException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "ConflictException");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handle(
            final UnauthorizedException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "UnauthorizedException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handle(
            final ForbiddenException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "ForbiddenException");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(
            final MethodArgumentNotValidException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        logException("warn", e, handler, request, "MethodArgumentNotValidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handle(
            final PaymentException e,
            final HandlerMethod handler,
            final HttpServletRequest request
    ) {
        if (e.is5xxServerError()) {
            logException("error", e, handler, request, "PaymentException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        logException("warn", e, handler, request, "PaymentException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    private void logException(
            final String level,
            final Exception e,
            final HandlerMethod handler,
            final HttpServletRequest request,
            final String exceptionName
    ) {
        final String logMessage = String.format(
                "[ERROR][CONTROLLER] %s.%s | method=%s uri=%s exception=%s message=%s",
                handler.getBeanType().getSimpleName(),
                handler.getMethod().getName(),
                request.getMethod(),
                request.getRequestURI(),
                exceptionName,
                e.getMessage()
        );

        if ("error".equalsIgnoreCase(level)) {
            log.error(logMessage, e);
        } else {
            log.warn(logMessage);
        }
    }
}
