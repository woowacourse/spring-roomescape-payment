package roomescape.payment.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final ApiClient apiClient;

    public PaymentService(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void payment(final String paymentKey, final String orderId,
                        final Integer amount, final String paymentType) {
        apiClient.authPayment(paymentKey, orderId, amount, paymentType);
    }
}
