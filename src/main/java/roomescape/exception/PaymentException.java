package roomescape.exception;

import lombok.Getter;
import roomescape.infrastructure.payment.PaymentErrorCode;

@Getter
public class PaymentException extends RuntimeException {
    private final PaymentErrorCode errorCode;

    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public PaymentException(PaymentErrorCode errorCode, String message) {
        super(errorCode.getMessage() + ": " + message);
        this.errorCode = errorCode;
    }
}
