package roomescape.payment;

import roomescape.exception.RoomescapeException;
import roomescape.payment.dto.PaymentErrorResponse;

public class TossPaymentException extends RoomescapeException {

    private final PaymentErrorResponse errorResponse;

    public TossPaymentException(final PaymentErrorResponse errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }

    public PaymentErrorResponse getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String toString() {
        return "TossPaymentException{" +
                "errorResponse=" + errorResponse +
                '}';
    }
}
