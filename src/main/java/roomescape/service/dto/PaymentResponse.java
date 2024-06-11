package roomescape.service.dto;

import roomescape.domain.payment.Payment;

public record PaymentResponse(
        Long reservationId,
        Long memberId,
        String paymentKey,
        String orderId,
        Integer amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getReservationId(), payment.getMemberId(), payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
