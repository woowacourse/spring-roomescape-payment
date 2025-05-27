package roomescape.reservation.dto.response;

import roomescape.reservation.entity.Payment;

public record PaymentApproveResponse(
        String paymentKey,
        String orderId
) {
}
