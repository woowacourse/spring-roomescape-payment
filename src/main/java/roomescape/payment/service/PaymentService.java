package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.request.PaymentCommand;
import roomescape.payment.infrastructure.PaymentClient;
import roomescape.reservation.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void payment(final PaymentRequest request) {
        paymentClient.authPayment(PaymentCommand.createByPaymentRequest(request));
    }
}
