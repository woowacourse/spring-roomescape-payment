package roomescape.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.common.response.ApiResponse;
import roomescape.payment.infrastructure.toss.exception.TossException;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleException(BusinessException e) {
        log.warn("[BusinessException] {} - status: {}", e.getMessage(), e.getStatus(), e);
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            message += fieldError.getDefaultMessage();
        }

        ApiResponse<?> response = ApiResponse.createError(message);
        log.warn("[ValidationException] 유효성 검사 실패 - {}", message, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TossException.class)
    public ResponseEntity<ApiResponse<?>> handleTossException(TossException e) {
        log.error("[TossException] Toss API 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.createError(errorCode.getMessage()));
    }
}
