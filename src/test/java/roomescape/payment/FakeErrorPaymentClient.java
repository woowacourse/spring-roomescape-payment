package roomescape.payment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

@Component
@Profile("test")
public class FakeErrorPaymentClient implements PaymentClient {

    @Override
    public TossPaymentResponse requestPaymentApprove(TossPaymentRequest request) {
        return new TossPaymentResponse("test", "test", 1L, PaymentType.NORMAL);
    }
}
