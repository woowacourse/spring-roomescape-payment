package roomescape.payment.infrastructure.toss;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.infrastructure.toss.client.TossPaymentClient;

@Service
@AllArgsConstructor
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossPaymentClient tossPaymentClient;

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        tossPaymentClient.approve(orderId, amount, paymentKey);
    }
}
