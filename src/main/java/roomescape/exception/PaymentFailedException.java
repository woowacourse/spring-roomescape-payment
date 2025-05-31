package roomescape.exception;

import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentFailCode.Cause;

public class PaymentFailedException extends RuntimeException {

    private final PaymentStatus paymentStatus;

    public PaymentFailedException(final PaymentStatus status) {
        super(status.message());
        this.paymentStatus = status;
    }

    public boolean causedByClient() {
        return paymentStatus.causedBy(Cause.CLIENT_ERROR);
    }
}
