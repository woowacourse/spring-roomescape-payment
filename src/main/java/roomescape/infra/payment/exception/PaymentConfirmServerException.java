package roomescape.infra.payment.exception;

import roomescape.exception.ApplicationException;

public class PaymentConfirmServerException extends ApplicationException {

    public PaymentConfirmServerException(String message) {
        super(message);
    }
}
