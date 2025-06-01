package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.PaymentInfo;
import roomescape.dto.request.PaymentRequest;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentInfo createPaymentInfo(PaymentRequest paymentRequest) {
        return paymentClient.postPaymentInfo(paymentRequest);
    }
}
