package roomescape.service.request;

import java.math.BigDecimal;
import roomescape.domain.Payment;
import roomescape.web.controller.request.MemberReservationRequest;

public record PaymentApproveAppRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {

    public static PaymentApproveAppRequest from(MemberReservationRequest request) {
        return new PaymentApproveAppRequest(request.paymentKey(), request.orderId(), request.amount());
    }

    public Payment toPayment() {
        return new Payment(null, paymentKey, orderId, amount);
    }
}
