package roomescape.utils;

import org.springframework.context.annotation.Profile;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.infrastructure.PaymentClient;

@Profile("test")
public class FakePaymentClient implements PaymentClient {
    public PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest) {
        return new PaymentConfirmResponse(1000, "orderId", "paymentKey");
    }
}