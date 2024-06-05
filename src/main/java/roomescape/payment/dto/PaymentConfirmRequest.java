package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.reservation.dto.UserReservationCreateRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount,
        Long reservationId
) {
    public static PaymentConfirmRequest from(UserReservationCreateRequest request, Long reservationId) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount(), reservationId);
    }

    public Payment createPayment() {
        return new Payment(reservationId, paymentKey, amount);
    }
}
