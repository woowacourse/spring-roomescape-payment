package roomescape.infrastructure.payment.toss;

import roomescape.exception.ExternalApiException;

public class TossPaymentException extends ExternalApiException {

    public TossPaymentException(TossPaymentErrorCode errorCode) {
        super(errorCode);
    }
}
