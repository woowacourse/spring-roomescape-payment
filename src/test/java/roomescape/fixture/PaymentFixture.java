package roomescape.fixture;

import static roomescape.fixture.MemberFixture.DEFAULT_MEMBER;

import roomescape.domain.payment.Payment;
import roomescape.dto.PaymentApproveRequest;

public class PaymentFixture {
    public static final Payment DEFAULT_PAYMENT_WITHOUT_ID = new Payment(
            "orderId", "paymentKey", 1000, DEFAULT_MEMBER);
    public static final PaymentApproveRequest DEFAULT_APPROVE_REQUEST = new PaymentApproveRequest(
            "paymentKey", "orderId", 1000
    );
}
