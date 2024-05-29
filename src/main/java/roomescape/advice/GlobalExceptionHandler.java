package roomescape.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.advice.dto.ErrorResponse;
import roomescape.auth.exception.AdminAuthorizationException;
import roomescape.auth.exception.AuthenticationException;
import roomescape.paymenthistory.exception.PaymentException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String NULL_POINTER_EXCEPTION_ERROR_MESSAGE = "인자 중 null 값이 존재합니다.";
    private static final String UNEXPECTED_EXCEPTION_ERROR_MESSAGE = "예상치 못한 예외가 발생했습니다. 관리자에게 문의하세요.";
    private static final String DATA_INTEGRITY_VIOLATION_EXCEPTION_ERROR_MESSAGE = "제약 조건에 어긋난 요청입니다.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(NULL_POINTER_EXCEPTION_ERROR_MESSAGE));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AdminAuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAdminAuthorizationException(AdminAuthorizationException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(DATA_INTEGRITY_VIOLATION_EXCEPTION_ERROR_MESSAGE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(UNEXPECTED_EXCEPTION_ERROR_MESSAGE));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        logger.error(e.getMessage(), e.getStackTrace(), e);
        return ResponseEntity.status(e.getHttpStatusCode())
                .body(new ErrorResponse(e.getMessage()));
    }
}
