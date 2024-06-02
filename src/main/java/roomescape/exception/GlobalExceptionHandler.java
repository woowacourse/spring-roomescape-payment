package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    private ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[{}] {}", e.getClass().getName(), e.getMessage());
        String errorMessage = "요청 본문을 읽을 수 없습니다. 요청 형식을 확인해 주세요.";
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    private ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            log.error("[{}] {}", e.getClass().getName(), cause.getMessage()
            );
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = PaymentException.class)
    private ProblemDetail handlePaymentException(PaymentException e) {
        log.error("[{}] statusCode: {}, message: {}, paymentKey: {}",
                e.getExceptionClass(),
                e.getServerStatusCode(),
                e.getMessage(),
                e.getPaymentKey()
        );
        return ProblemDetail.forStatusAndDetail(e.getClientStatusCode(), e.getMessage());
    }

    @ExceptionHandler(value = PaymentInternalException.class)
    private ProblemDetail handlePaymentInternalException(PaymentInternalException e) {
        log.error("[{}] {}", e.getExceptionClass(), e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }

    @ExceptionHandler(value = Exception.class)
    private ProblemDetail handleGeneralException(Exception e) {
        log.error("[{}] {}", e.getClass().getName(), e.getMessage());
        String errorMessage = "시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
