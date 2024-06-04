package roomescape.payment.config;

import roomescape.payment.dto.TossErrorResponse;

public class PaymentException extends RuntimeException {

    public PaymentException(TossErrorResponse tossErrorResponse) {
        super(tossErrorResponse.message());
    }
}
