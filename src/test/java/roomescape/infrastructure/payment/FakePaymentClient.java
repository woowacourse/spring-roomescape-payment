package roomescape.infrastructure.payment;

import org.springframework.stereotype.Component;
import roomescape.domain.dto.PaymentCancelRequest;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

@Component
public class FakePaymentClient implements PaymentClient {

    @Override
    public Payment approve(PaymentRequest request) {
        return new Payment(request.paymentKey(), request.amount(), request.orderId(), "", "");
    }

    @Override
    public void cancel(PaymentCancelRequest request) {
    }
}
