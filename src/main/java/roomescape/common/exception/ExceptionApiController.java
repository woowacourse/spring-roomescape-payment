package roomescape.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionApiController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        log.error("[IllegalArgumentException] ", exception);

        return createErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception) {
        log.error("[HttpMessageNotReadableException] ", exception);

        return createErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("[MethodArgumentNotValidException]", exception);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TossPaymentException.class)
    public ResponseEntity<ProblemDetail> tossPaymentExceptionHandler(TossPaymentException exception) {
        log.error("[PaymentException]", exception);

        return createErrorResponse(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler(PaymentSaveFailureException.class)
    public ResponseEntity<ProblemDetail> paymentSaveFailureExceptionHandler(PaymentSaveFailureException exception) {
        log.error("[PaymentSaveFailureException]", exception);

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> exceptionHandler(Exception exception) {
        log.error("[Exception]", exception);

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");
    }

    private ResponseEntity<ProblemDetail> createErrorResponse(HttpStatus httpStatus, String errorMessage) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, errorMessage);
        return new ResponseEntity<>(problemDetail, httpStatus);
    }
}
