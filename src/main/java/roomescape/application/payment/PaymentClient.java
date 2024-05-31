package roomescape.application.payment;

import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.exception.payment.PaymentException;

public class PaymentClient {
    private final RestClient client;

    public PaymentClient(RestClient client) {
        this.client = client;
    }

    public Payment requestPurchase(PaymentRequest request) {
        Payment payment = client.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(Payment.class);
        if (payment == null) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        return payment;
    }
}
