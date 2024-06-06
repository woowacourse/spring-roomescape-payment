package roomescape.service.request;

import java.math.BigDecimal;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.web.controller.request.MemberReservationRequest;

public record PaymentApproveAppRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {

    public static PaymentApproveAppRequest from(MemberReservationRequest request) {
        return new PaymentApproveAppRequest(request.paymentKey(), request.orderId(), request.amount());
    }

    public Payment toPaymentWith(Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, amount);
    }
}
