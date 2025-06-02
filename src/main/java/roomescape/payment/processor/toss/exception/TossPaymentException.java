package roomescape.payment.processor.toss.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.payment.exception.PaymentException;

public class TossPaymentException extends PaymentException {
    public TossPaymentException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
