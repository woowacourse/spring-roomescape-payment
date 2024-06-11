package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.UserReservationCreateRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount,
        Long reservationId,
        Long memberId
) {
    public static PaymentConfirmRequest from(UserReservationCreateRequest request, Long reservationId, Long memberId) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount(), reservationId, memberId);
    }

    public static PaymentConfirmRequest from(ReservationPaymentRequest request, Long reservationId, Long memberId) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount(), reservationId, memberId);
    }

    public Payment createPayment() {
        return new Payment(reservationId, memberId, orderId, paymentKey, amount);
    }
}
