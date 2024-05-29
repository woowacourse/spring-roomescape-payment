package roomescape.payment;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@TestComponent
@Primary
public class FakePaymentClient implements PaymentClient {

    @Override
    public Payment confirm(final ConfirmPaymentRequest confirmPaymentRequest) {
        return new Payment(confirmPaymentRequest.paymentKey(), confirmPaymentRequest.orderId(),
                confirmPaymentRequest.amount());
    }
}
