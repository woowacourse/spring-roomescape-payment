package roomescape.infrastructure.payment;

import org.springframework.web.client.RestClient;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.dto.PaymentResponse;
import roomescape.application.payment.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.exception.payment.PaymentException;

public class TossPaymentClient implements PaymentClient {
    private final RestClient client;

    public TossPaymentClient(RestClient client) {
        this.client = client;
    }

    @Override
    public Payment requestPurchase(PaymentRequest request) {
        PaymentResponse payment = client.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
        if (payment == null) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        return new Payment(payment.orderId(), payment.paymentKey(), payment.totalAmount());
    }
}
