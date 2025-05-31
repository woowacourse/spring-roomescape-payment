package roomescape.exception;

import roomescape.domain.payment.PaymentFailure;
import roomescape.domain.payment.PaymentFailCode.Cause;

public class PaymentFailedException extends RuntimeException {

    private final PaymentFailure paymentFailure;

    public PaymentFailedException(final PaymentFailure failure) {
        super(failure.message());
        this.paymentFailure = failure;
    }

    public boolean causedByClient() {
        return paymentFailure.causedBy(Cause.CLIENT_ERROR);
    }
}
