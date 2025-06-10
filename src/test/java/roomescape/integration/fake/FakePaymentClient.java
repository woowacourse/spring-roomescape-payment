package roomescape.integration.fake;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.request.TossPaymentRequest;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;

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
