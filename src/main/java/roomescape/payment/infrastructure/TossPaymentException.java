package roomescape.payment.infrastructure;

import org.springframework.http.HttpStatus;
import roomescape.payment.application.PaymentException;

public class TossPaymentException extends PaymentException {

    public TossPaymentException(final HttpStatus status, final String message) {
        super(status, message);
    }
}
