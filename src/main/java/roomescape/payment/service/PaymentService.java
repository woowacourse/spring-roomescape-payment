package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.TossPayRestClient;
import roomescape.payment.dto.PaymentRequest;

@Service
public class PaymentService {

    private final TossPayRestClient tossPayRestClient;

    public PaymentService(TossPayRestClient tossPayRestClient) {
        this.tossPayRestClient = tossPayRestClient;
    }

    public void pay(PaymentRequest paymentRequest) {
        tossPayRestClient.pay(paymentRequest);
    }
}
