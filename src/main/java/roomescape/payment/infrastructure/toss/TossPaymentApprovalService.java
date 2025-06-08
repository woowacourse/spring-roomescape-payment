package roomescape.payment.infrastructure.toss;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.application.dto.PaymentApprovalRequest;
import roomescape.payment.infrastructure.toss.client.TossRestClient;

@Component
@AllArgsConstructor
@Slf4j
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossRestClient tossRestClient;

    @Override
    public void approvePayment(PaymentApprovalRequest paymentApprovalRequest) {
        tossRestClient.approve(paymentApprovalRequest);
        log.info("토스 결제 요청 성공");
    }
}
