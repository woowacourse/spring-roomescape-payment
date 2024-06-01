package roomescape.payment;

import roomescape.exception.RoomescapeException;
import roomescape.payment.dto.PaymentErrorResponse;

public class TossPaymentException extends RoomescapeException { //TODO 예외 처리 전체적으로 리팩터링 필요

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
