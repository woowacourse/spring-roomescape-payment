package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.infrastructure.TossApiClient;

@Service
public class PaymentService {

    private final TossApiClient tossApiClient;

    public PaymentService(final TossApiClient tossApiClient) {
        this.tossApiClient = tossApiClient;
    }

    public void payment(final String paymentKey, final String orderId,
                        final Integer amount, final String paymentType) {
        tossApiClient.authPayment(paymentKey, orderId, amount, paymentType);
    }
}
