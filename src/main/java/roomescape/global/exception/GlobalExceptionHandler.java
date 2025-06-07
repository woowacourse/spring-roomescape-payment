package roomescape.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import roomescape.global.exception.authentication.AuthenticationException;
import roomescape.global.exception.payment.PaymentException;
import roomescape.global.exception.roomescape.RoomEscapeException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomEscapeException.class)
    public ResponseEntity<ErrorResponse> handleRoomEscapeException(final RoomEscapeException e) {
        String requestInfo = getRequestInfo();
        log.warn("[ROOM_ESCAPE_ERROR] {} | {}", requestInfo, e.getMessage());

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        String requestInfo = getRequestInfo();
        log.error("[PAYMENT_ERROR] {} | {}", requestInfo, e.getMessage());

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException e) {
        String requestInfo = getRequestInfo();
        log.warn("[AUTH_ERROR] {} | {}", requestInfo, e.getMessage());

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValidException(final MethodArgumentNotValidException e) {
        String requestInfo = getRequestInfo();
        final String errors = e.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("[VALIDATION_ERROR] {} | {}", requestInfo, errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        String requestInfo = getRequestInfo();
        log.error("[UNEXPECTED_ERROR] {} | {}", requestInfo, e.getMessage(), e);

        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getMessage()));
    }

    private String getRequestInfo() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return String.format("[%s] %s", request.getMethod(), request.getRequestURI());
            }
        } catch (Exception e) {
            log.debug("요청 정보를 가져올 수 없습니다: {}", e.getMessage());
        }
        return "[UNKNOWN]";
    }
}
