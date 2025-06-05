package roomescape.payment.exception;

import roomescape.common.exception.DomainStatusException;

public class PaymentStatusException extends DomainStatusException {

    public PaymentStatusException(final String message) {
        super(message);
    }
}
