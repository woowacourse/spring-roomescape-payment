package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

import java.time.LocalDateTime;

@Service
public class PaymentApplicationService {

    private final PaymentDataService paymentDataService;
    private final PaymentClient paymentClient;

    public PaymentApplicationService(PaymentDataService paymentDataService, final PaymentClient paymentClient) {
        this.paymentDataService = paymentDataService;
        this.paymentClient = paymentClient;
    }

    public Payment approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        PaymentApproveResponse paymentApproveResponse = paymentClient.approvePayment(paymentApproveRequest);
        Payment payment = new Payment(paymentApproveResponse.paymentKey(), paymentApproveResponse.orderId(), paymentApproveResponse.totalAmount(), LocalDateTime.now());
        return paymentDataService.save(payment);
    }
}
