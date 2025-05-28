package roomescape.payment.infrastructure;

import roomescape.payment.application.PaymentException;

public class TossPaymentException extends PaymentException {

    private final String code;

    public TossPaymentException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
