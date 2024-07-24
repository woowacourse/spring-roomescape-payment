package roomescape.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.exception.custom.PaymentCancelException;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.custom.PaymentInternalException;
import roomescape.exception.custom.RoomEscapeException;

@ControllerAdvice
class GlobalExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    private ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. 요청 형식을 확인해 주세요.");
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    private ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @ExceptionHandler(value = RoomEscapeException.class)
    private ProblemDetail handleRoomEscapeException(RoomEscapeException e) {
        log.error(e.getMessage(), e);

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = PaymentException.class)
    private ProblemDetail handlePaymentException(PaymentException e) throws JsonProcessingException {
        String paymentRequest = objectMapper.writeValueAsString(e.getPaymentRequest());
        log.error(e.getClientStatusCode().toString(), e.getMessage(), paymentRequest, e.getException());

        return ProblemDetail.forStatusAndDetail(e.getClientStatusCode(), e.getMessage());
    }

    @ExceptionHandler(value = PaymentCancelException.class)
    private ProblemDetail handlePaymentCancelException(PaymentCancelException e) throws JsonProcessingException {
        String cancelRequest = objectMapper.writeValueAsString(e.getCancelRequest());
        log.error(e.getClientStatusCode().toString(), e.getMessage(), cancelRequest, e.getException());

        return ProblemDetail.forStatusAndDetail(e.getClientStatusCode(), e.getMessage());
    }

    @ExceptionHandler(value = PaymentInternalException.class)
    private ProblemDetail handlePaymentInternalException(PaymentInternalException e) {
        log.error(e.getMessage(), e.getException());

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @ExceptionHandler(value = Exception.class)
    private ProblemDetail handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }
}
