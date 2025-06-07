package roomescape.payment.application;


import roomescape.approval.application.command.PaymentCommandService;
import roomescape.approval.infrastructure.toss.dto.TossPaymentApprovalRequest;

public class FakePaymentCommandService implements PaymentCommandService {

    @Override
    public void approvePayment(TossPaymentApprovalRequest request) {
        return;
    }
}