package roomescape.payment.infrastructure;

import lombok.Getter;
import roomescape.payment.application.PaymentException;

@Getter
public class TossPaymentException extends PaymentException {

    private final String code;

    public TossPaymentException(final String code, final String message) {
        super(message);
        this.code = code;
    }
}
