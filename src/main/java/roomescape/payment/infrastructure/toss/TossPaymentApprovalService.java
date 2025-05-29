package roomescape.payment.infrastructure.toss;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.infrastructure.toss.client.TossRestClient;

@Component
@AllArgsConstructor
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossRestClient tossRestClient;

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        tossRestClient.approve(orderId, amount, paymentKey);
    }
}
