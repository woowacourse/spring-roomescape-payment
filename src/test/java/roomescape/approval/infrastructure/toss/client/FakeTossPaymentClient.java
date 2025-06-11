package roomescape.approval.infrastructure.toss.client;

import roomescape.approval.infrastructure.toss.dto.TossPaymentApprovalRequest;

public class FakeTossPaymentClient extends TossPaymentClient {

    public FakeTossPaymentClient() {
        super(null, null);
    }

    @Override
    public void approve(TossPaymentApprovalRequest approvalRequest) {
    }
}