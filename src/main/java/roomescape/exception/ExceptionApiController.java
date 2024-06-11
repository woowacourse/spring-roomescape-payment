package roomescape.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.payment.config.PaymentException;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionApiController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionInfo> illegalArgExHandler(IllegalArgumentException exception) {
        ExceptionInfo exceptionInfo = new ExceptionInfo(exception.getMessage());

        return ResponseEntity.badRequest().body(exceptionInfo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentExHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionInfo> paymentExHandler(PaymentException paymentException) {
        ExceptionInfo exceptionInfo = new ExceptionInfo(paymentException.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionInfo);
    }
}
