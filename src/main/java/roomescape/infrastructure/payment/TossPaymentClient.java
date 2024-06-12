package roomescape.infrastructure.payment;

import org.springframework.web.client.RestClient;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.dto.PaymentClientRequest;
import roomescape.application.payment.dto.PaymentClientResponse;
import roomescape.domain.payment.Payment;
import roomescape.exception.payment.PaymentException;

public class TossPaymentClient implements PaymentClient {
    private final RestClient client;

    public TossPaymentClient(RestClient client) {
        this.client = client;
    }

    @Override
    public Payment requestPurchase(PaymentClientRequest request) {
        PaymentClientResponse response = client.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(PaymentClientResponse.class);
        if (response == null || response.isNotSuccessful()) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        return new Payment(response.orderId(), response.paymentKey(), response.totalAmount());
    }
}
