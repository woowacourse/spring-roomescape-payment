package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.response.PaymentResponse;

@Service
public class PaymentService {

    private final PaymentApiClient paymentApiClient;

    public PaymentService(final PaymentApiClient paymentApiClient) {
        this.paymentApiClient = paymentApiClient;
    }

    public void payment(final String paymentKey, final String orderId,
                        final Integer amount, final String paymentType) {
        PaymentResponse paymentResponse = paymentApiClient.authPayment(paymentKey, orderId, amount, paymentType);
    }
}
