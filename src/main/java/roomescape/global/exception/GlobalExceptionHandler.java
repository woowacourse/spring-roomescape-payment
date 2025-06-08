package roomescape.global.exception;

import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.global.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(final ExternalApiException exception) {
        log.error("외부 API 예외 발생: {}", exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatus()).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(final AuthenticationException exception) {
        log.warn("인증 실패: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(final AuthenticationException exception) {
        log.warn("접근 거부: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(final NoSuchElementException exception) {
        log.warn("요소를 찾을 수 없음: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.warn("잘못된 요청 파라미터: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(final IllegalStateException exception) {
        log.warn("잘못된 상태: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationExceptions(
            final MethodArgumentNotValidException exception
    ) {
        log.warn("검증 실패: {}", exception.getMessage(), exception);
        List<ErrorResponse> errorResponses = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse(
                        String.format("%s:%s", error.getField(), error.getDefaultMessage())
                ))
                .toList();
        return ResponseEntity.badRequest().body(errorResponses);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(final Exception exception) {
        log.error("서버 에러 발생: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("예상치 못한 서버 에러가 발생했습니다.")
        );
    }
}
