package roomescape.utils;

import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.infrastructure.PaymentClient;

public class FakePaymentClient implements PaymentClient {
    public PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest) {
        return new PaymentConfirmResponse(1000, "orderId", "paymentKey");
    }
}