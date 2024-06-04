package roomescape.fixture;

import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;

import roomescape.domain.payment.Payment;
import roomescape.dto.ApproveApiResponse;
import roomescape.dto.PaymentApproveRequest;

public class PaymentFixture {
    public static final Payment DEFAULT_PAYMENT_WITHOUT_ID = new Payment("orderId", "paymentKey", 1000);
    public static final Payment DEFAULT_PAYMENT = new Payment(1L, "orderId", "paymentKey", 100,
            DEFAULT_RESERVATION);
    public static final PaymentApproveRequest DEFAULT_APPROVE_REQUEST = new PaymentApproveRequest(
            "paymentKey", "orderId", 1000, DEFAULT_RESERVATION.getId()
    );
    public static final ApproveApiResponse DEFAULT_APPROVE_RESPONSE = new ApproveApiResponse(
            DEFAULT_APPROVE_REQUEST.orderId(), DEFAULT_APPROVE_REQUEST.paymentKey(), DEFAULT_APPROVE_REQUEST.amount()
    );
}
