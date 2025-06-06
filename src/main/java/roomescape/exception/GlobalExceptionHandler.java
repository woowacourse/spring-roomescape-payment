package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.auth.AuthTokenNotFoundException;
import roomescape.exception.auth.AuthorizationException;
import roomescape.exception.payment.PaymentException;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.exception.resource.ResourceNotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Object> handlePaymentException(final PaymentException e) {
        log.warn("결제 예외 발생 - 코드: {}, 메시지: {}", e.getStatus(), e.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getStatus(), "유효성 검증에 실패했습니다.");

        return ResponseEntity.status(e.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(final ResourceNotFoundException e) {
        log.warn("리소스를 찾을 수 없음 - {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Void> handleAlreadyExistException(final AlreadyExistException e) {
        log.warn("이미 존재하는 리소스 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(AuthTokenNotFoundException.class)
    public ResponseEntity<Void> handleAuthTokenNotFoundException(final AuthTokenNotFoundException e) {
        log.warn("인증 토큰 누락 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Void> handleAuthorizationException(final AuthorizationException e) {
        log.warn("인가 실패 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("잘못된 요청 파라미터 - {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(final Exception e) {
        log.error("예상치 못한 시스템 예외 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
