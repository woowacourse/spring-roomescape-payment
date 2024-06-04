package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentConfirmResponse confirm(PaymentConfirmRequest paymentConfirmRequest) {
        return paymentClient.confirm(paymentConfirmRequest);
    }
}
