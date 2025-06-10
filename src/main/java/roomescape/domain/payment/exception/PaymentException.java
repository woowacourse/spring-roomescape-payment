package roomescape.domain.payment.exception;

import roomescape.infrastructure.exception.DomainException;

public class PaymentException extends DomainException {
    public PaymentException(final String message) {
        super(message);
    }
}
