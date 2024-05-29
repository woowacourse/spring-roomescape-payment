package roomescape.utils;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.infrastructure.PaymentProvider;
import roomescape.infrastructure.PaymentSecretKeyEncoder;

@Component
@Primary
@Profile("test")
public class FakePaymentProvider extends PaymentProvider {
    public FakePaymentProvider(final PaymentSecretKeyEncoder encoder, final RestClient restClient) {
        super(encoder, restClient);
    }

    public PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest) {
        return new PaymentConfirmResponse(1000, "orderId", "paymentKey");
    }
}
