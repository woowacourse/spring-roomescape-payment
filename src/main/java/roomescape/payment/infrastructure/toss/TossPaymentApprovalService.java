package roomescape.payment.infrastructure.toss;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.application.dto.PaymentApprovalRequest;
import roomescape.payment.infrastructure.toss.client.TossPaymentClient;

@Service
@AllArgsConstructor
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossPaymentClient tossPaymentClient;

    @Override
    public void approvePayment(PaymentApprovalRequest request) {
        tossPaymentClient.approve(request);
    }
}
