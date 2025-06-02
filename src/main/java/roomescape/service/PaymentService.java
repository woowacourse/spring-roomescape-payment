package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.PaymentClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest) {
        return paymentClient.confirmPayment(paymentRequest);
    }
}
