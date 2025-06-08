package roomescape.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import roomescape.common.response.ApiResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleException(BusinessException e) {
        logByStatus(e.getStatus(), e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            message += fieldError.getDefaultMessage();
        }

        logByStatus(HttpStatus.BAD_REQUEST, message, e);
        ApiResponse<?> response = ApiResponse.createError(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            ConversionFailedException.class
    })
    public ResponseEntity<ApiResponse<?>> handleTypeMismatchException(Exception e) {
        logByStatus(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        ApiResponse<?> response = ApiResponse.createError("요청 파라미터 형식이 잘못되었습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<?>> handleTossException(ExternalApiException e) {
        logByStatus(e.getStatus(), e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.createError(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logByStatus(errorCode.getStatus(), e.getMessage(), e);
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.createError(errorCode.getMessage()));
    }

    private void logByStatus(HttpStatus status, String message, Throwable e) {
        if (status.is5xxServerError()) {
            log.error("Server error ({}): {}", status.value(), message, e);
            return;
        }

        log.warn("Client error ({}): {}", status.value(), message);

    }
}
