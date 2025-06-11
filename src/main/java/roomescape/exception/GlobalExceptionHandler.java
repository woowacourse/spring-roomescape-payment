package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse body = new ErrorResponse(e.getMessage());
        createExceptionLog(e);
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(value = PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        ErrorResponse body = new ErrorResponse(e.getMessage());
        createExceptionLog(e);
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(value = ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleSocketException(ResourceAccessException e) {
        ErrorResponse body = new ErrorResponse("연결에 실패했습니다.");
        createExceptionLog(e);
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(body);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        String validationFields = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .toList().toString();

        String validationExceptionMessage = String.format("%s, %s", errorMessage, validationFields);
        ErrorResponse body = new ErrorResponse(errorMessage);
        createExceptionLog(e, validationExceptionMessage);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        createExceptionLog(e);
        ErrorResponse body = new ErrorResponse("잘못된 요청 형식입니다.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        createExceptionLog(e);
        ErrorResponse body = new ErrorResponse("오류가 발생하였습니다. 관리자에게 문의해주세요");
        return ResponseEntity.internalServerError().body(body);
    }

    private void createExceptionLog(Exception exception) {
        LOGGER.warn("[예외 발생] {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
    }

    private void createExceptionLog(Exception exception, String message) {
        LOGGER.warn("[예외 발생] {}: {}", exception.getClass().getSimpleName(), message);
    }
}
