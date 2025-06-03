package roomescape.payment.application;

import roomescape.payment.application.dto.PaymentApprovalRequest;

public interface PaymentApprovalService {
    void approvePayment(PaymentApprovalRequest request);
}
