package roomescape.payment.controller.dto.response;

import roomescape.payment.domain.Payment;

public record PaymentResponse(
        long id,
        String paymentKey,
        String orderId,
        long amount,
        long reservationId
) {

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getReservation().getId()
        );
    }
}
