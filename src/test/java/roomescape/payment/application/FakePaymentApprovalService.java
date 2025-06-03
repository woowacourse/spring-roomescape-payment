package roomescape.payment.application;


import roomescape.payment.application.dto.PaymentApprovalRequest;

public class FakePaymentApprovalService implements PaymentApprovalService {

    @Override
    public void approvePayment(PaymentApprovalRequest request) {
        return;
    }
}