package roomescape.exception;

import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentFailCode.Cause;

public class PaymentFailedException extends RuntimeException {

    private final PaymentFailCode failCode;

    public PaymentFailedException(final PaymentFailCode failCode, final String message) {
        super(message);
        this.failCode = failCode;
    }

    public boolean causedByClient() {
        return failCode.causedBy(Cause.CLIENT_ERROR);
    }

    public boolean causedByServer() {
        return failCode.causedBy(Cause.SERVER_ERROR);
    }

    public boolean causedByExternalServer() {
        return failCode.causedBy(Cause.EXTERNAL_ERROR);
    }

    public PaymentFailCode getFailCode() {
        return failCode;
    }
}
