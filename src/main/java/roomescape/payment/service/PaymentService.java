package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public Payment createPayment(ConfirmPaymentRequest paymentRequest) {
        return paymentClient.confirm(paymentRequest);
    }
}
