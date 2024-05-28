package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.config.TossPaymentRestClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;

@Service
public class PaymentService {

    private final TossPaymentRestClient tossPaymentRestClient;

    public PaymentService(TossPaymentRestClient tossPaymentRestClient) {
        this.tossPaymentRestClient = tossPaymentRestClient;
    }

    public PaymentResponse askPayment(PaymentRequest request) {
        return tossPaymentRestClient.pay(request);
    }
}
