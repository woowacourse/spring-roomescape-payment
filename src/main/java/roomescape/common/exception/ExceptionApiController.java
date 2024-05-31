package roomescape.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionApiController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> IllegalArgExHandler(IllegalArgumentException exception) {
        log.error("[IllegalArgumentException] ", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentExHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("[MethodArgumentNotValidException]", exception);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ProblemDetail> paymentExHandler(PaymentException exception) {
        log.error("[PaymentException]", exception);

        return createErrorResponse(exception.getStatus(), exception.getMessage());
    }

    private ResponseEntity<ProblemDetail> createErrorResponse(HttpStatus httpStatus, String errorMessage) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, errorMessage);
        return new ResponseEntity<>(problemDetail, httpStatus);
    }
}
