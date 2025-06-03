package roomescape.exception.custom.reason.payment;

import roomescape.payment.dto.PaymentError;
import roomescape.exception.custom.status.BadRequestException;

public class PaymentConfirmException extends BadRequestException {
    public PaymentConfirmException(final PaymentError error) {
        super(error.formatMessage());
    }
}
