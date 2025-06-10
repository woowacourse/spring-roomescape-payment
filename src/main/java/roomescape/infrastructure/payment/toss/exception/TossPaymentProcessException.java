package roomescape.infrastructure.payment.toss.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.domain.payment.exception.PaymentProcessException;

public class TossPaymentProcessException extends PaymentProcessException {
    public TossPaymentProcessException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
