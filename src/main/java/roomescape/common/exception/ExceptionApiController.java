package roomescape.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.reservation.client.errorcode.PaymentConfirmCustomException;
import roomescape.reservation.controller.dto.response.PaymentErrorResponse;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionApiController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionApiController.class);

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleBadRequestExceptions(RuntimeException exception) {
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

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentErrorResponse> paymentExHandler(PaymentException exception) {
        logger.error(exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatusCode())
                .body(new PaymentErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PaymentConfirmCustomException.class)
    public ResponseEntity<String> paymentConfirmCustomException(
            PaymentConfirmCustomException exception
    ) {
        logger.error(exception.getMessage(), exception);
        return ResponseEntity.status(500).body("서버 내부 오류입니다.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionHandler(RuntimeException exception) {
        logger.error(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body("서버 내부 오류입니다.");
    }
}
