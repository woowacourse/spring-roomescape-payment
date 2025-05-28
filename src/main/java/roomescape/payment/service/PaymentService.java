package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.reservation.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final TossApiClient tossApiClient;

    public PaymentService(final TossApiClient tossApiClient) {
        this.tossApiClient = tossApiClient;
    }

    public void payment(final String paymentKey, final String orderId,
                        final Integer amount, final String paymentType) {
        PaymentRequest paymentRequest = new PaymentRequest(paymentKey, orderId, amount, paymentType);
        tossApiClient.authPayment(paymentRequest);
    }
}
