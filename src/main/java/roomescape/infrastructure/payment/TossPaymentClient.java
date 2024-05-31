package roomescape.infrastructure.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.exception.payment.PaymentException;

public class TossPaymentClient implements PaymentClient {
    private static final Logger logger = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient client;

    public TossPaymentClient(RestClient client) {
        this.client = client;
    }

    @Override
    public Payment requestPurchase(PaymentRequest request) {
        Payment payment = client.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(Payment.class);
        if (payment == null) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        logger.info("Payment response: {}", payment);
        return payment;
    }
}
