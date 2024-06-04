package roomescape.service.client;

import roomescape.global.exception.RoomescapeException;
import roomescape.service.dto.PaymentRequest;

public class FakeTossPaymentClient implements PaymentClient {
    @Override
    public void requestPayment(PaymentRequest body) {
        if (body.orderId().isBlank() || body.paymentKey().isBlank()) {
            throw new RoomescapeException("문제가 발생했습니다.");
        }
    }
}
