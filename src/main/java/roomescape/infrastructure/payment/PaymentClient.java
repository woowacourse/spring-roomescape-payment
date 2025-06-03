package roomescape.infrastructure.payment;

import roomescape.infrastructure.payment.dto.PaymentApproveRequest;

public interface PaymentClient {

    void approvePayment(PaymentApproveRequest paymentApproveRequest);
}
