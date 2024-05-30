package roomescape.infra.payment.exception;

import roomescape.exception.ApplicationException;

public class PaymentServerException extends ApplicationException {

    public PaymentServerException(String message) {
        super(message);
    }
}
