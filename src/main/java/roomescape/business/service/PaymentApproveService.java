package roomescape.business.service;

import org.springframework.stereotype.Service;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveRequest;
import roomescape.presentation.api.PaymentApproveRequest;

@Service
public class PaymentApproveService {

    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    public PaymentApproveService(PaymentService paymentService, PaymentClient paymentClient) {
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

    public void requestApproveAndCompletePayment(String paymentId, PaymentApproveRequest request) {
        paymentService.completePayment(paymentId, request.paymentKey(), request.amount());
        try {
            paymentClient.approvePayment(
                    new TossPaymentApproveRequest(request.paymentKey(), paymentId, request.amount()));
        } catch (RuntimeException e) {
            paymentService.deletePaymentById(paymentId);
            throw e;
        }
    }
}
