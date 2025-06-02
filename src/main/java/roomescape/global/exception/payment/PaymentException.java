package roomescape.global.exception.payment;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {

    public static final String SENSITIVE_EXCEPTION_MESSAGE = "결제 승인 중 예외가 발생하였습니다.";
    public static final String UNKNOWN_EXCEPTION_MESSAGE = "알 수 없는 결제 오류가 발생했습니다.";

    private final TossPaymentErrorCode tossPaymentErrorCode;

    public PaymentException(TossPaymentErrorCode tossPaymentErrorCode, String message) {
        super(createMessage(tossPaymentErrorCode, message));
        this.tossPaymentErrorCode = tossPaymentErrorCode;
    }

    private static String createMessage(TossPaymentErrorCode errorCode, String message) {
        return switch (errorCode.level) {
            case SENSITIVE -> SENSITIVE_EXCEPTION_MESSAGE;
            case SHOW_USER -> message;
            case UNKNOWN -> UNKNOWN_EXCEPTION_MESSAGE;
        };
    }

    public HttpStatus getHttpStatus() {
        return tossPaymentErrorCode.httpStatus;
    }
}
