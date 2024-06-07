package roomescape.infrastructure.payment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

@Component
@Profile("local")
public class FakePaymentClient implements PaymentClient {

    @Override
    public Payment approve(PaymentRequest request) {
        return new Payment(request.paymentKey(), request.amount(), request.orderId(), "", "");
    }
}
