package roomescape.exception;

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

@RestControllerAdvice(annotations = RestController.class)
public class ApiControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception){
        LOGGER.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body("잘못된 요청 형식입니다. 입력 값을 확인해주세요.");
    }

    @ExceptionHandler(ParsingFailException.class)
    public ResponseEntity<String> handleParsingFailException(ParsingFailException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body("서버 에러입니다.");
    }

    @ExceptionHandler(PaymentFailException.class)
    public ResponseEntity<PaymentErrorResponse> handlePaymentFailException(PaymentFailException exception) {
        LOGGER.error("토스 결제 에러 code: {}, message: {}", exception.getCode(), exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(new PaymentErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        LOGGER.error("서버 에러: {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body("서버 에러입니다.");
    }
}
