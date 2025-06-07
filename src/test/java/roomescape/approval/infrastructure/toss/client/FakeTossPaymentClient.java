package roomescape.approval.infrastructure.toss.client;

import org.springframework.stereotype.Component;
import roomescape.approval.infrastructure.toss.dto.TossPaymentApprovalRequest;

@Component
public class FakeTossPaymentClient extends TossPaymentClient {

    public FakeTossPaymentClient() {
        super(null, null);
    }

    @Override
    public void approve(TossPaymentApprovalRequest approvalRequest) {
    }
}