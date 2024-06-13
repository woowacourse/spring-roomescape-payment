package roomescape.global.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.global.exception.auth.AuthenticationException;
import roomescape.global.exception.auth.AuthorizationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버 관리자에게 문의하세요.";
    private static final String TOSS_PAYMENT_SERVER_ERROR_MESSAGE = "결제에 실패했습니다. 관리자에게 문의하세요.";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining());

        return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalRequestException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePaymentExceptionError(PaymentFailException e) {
        log.error("Toss Payment Error 발생: {}", e.getMessage(), e);
        if (e.isClientError()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
        return ResponseEntity.internalServerError().body(new ErrorResponse(TOSS_PAYMENT_SERVER_ERROR_MESSAGE));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e) {
        log.error("Internal Server Error 발생: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ErrorResponse(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
