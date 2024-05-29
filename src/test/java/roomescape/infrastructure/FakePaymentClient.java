package roomescape.infrastructure;

import java.time.LocalDateTime;
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
        return new Payment(request.paymentKey(), request.amount(), false, LocalDateTime.now().minusSeconds(5), LocalDateTime.now());
    }
}
