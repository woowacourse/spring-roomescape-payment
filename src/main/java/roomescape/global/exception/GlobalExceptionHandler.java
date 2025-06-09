package roomescape.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.global.dto.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(final BusinessException exception) {
        return ResponseEntity.status(exception.getErrorCode().status()).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(final RuntimeException exception,
                                                                final HttpServletRequest request) {
        log.error("[UNEXPECTED ERROR] 서버 에러 발생 - URI: {}, Method: {}, IP: {}, Message: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                exception.getMessage(),
                exception
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("예상치 못한 서버 에러가 발생했습니다.")
        );
    }
}
