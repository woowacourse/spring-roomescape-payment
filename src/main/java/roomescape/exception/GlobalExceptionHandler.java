package roomescape.exception;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.custom.UnauthorizedException;
import roomescape.exception.dto.ErrorResponse;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(e.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(exception = UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.error("UnauthorizedException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(e.getMessage());

        return ResponseEntity.status(UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(exception = ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        log.error("ForbiddenException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(e.getMessage());

        return ResponseEntity.status(FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(exception = PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        log.error("PaymentException 발생 - statusCode: {}, message: {}", e.getStatusCode(), e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(e.getMessage());

        return ResponseEntity.status(e.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 발생 - message: {}", e.getMessage(), e);
        List<ErrorResponse> responses = createValidationErrorMessage(e.getBindingResult());

        return ResponseEntity.badRequest().body(responses);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from("데이터베이스 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(RestClientException e) {
        log.error("RestClientException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from("외부 서비스 호출 중 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from("시스템 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from("시스템 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception 발생 - message: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from("알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    private List<ErrorResponse> createValidationErrorMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .map(ErrorResponse::from)
                .toList();
    }
}
