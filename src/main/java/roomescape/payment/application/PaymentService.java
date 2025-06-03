package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(final PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentApproveResponse approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        return paymentClient.approvePayment(paymentApproveRequest);
    }
}
