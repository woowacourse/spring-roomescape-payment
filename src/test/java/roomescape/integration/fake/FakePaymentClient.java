package roomescape.integration.fake;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.dto.request.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.response.TossPaymentResponse;

@Component
@Profile("test")
public class FakePaymentClient implements PaymentClient {

    @Override
    public TossPaymentResponse requestPayment(final TossPaymentRequest request) {
        return new TossPaymentResponse(
                "pay_fake_test_key",
                request.orderId(),
                request.amount(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }
}
