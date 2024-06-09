package roomescape.controller.dto.response;

import java.util.Optional;

import roomescape.domain.reservation.payment.Payment;

public record PaymentResponse(
        boolean isExists,
        Long id,
        String orderId,
        long amount,
        String paymentKey) {
    public static PaymentResponse EMPTY = new PaymentResponse(false, null, "", 0, "");

    public static PaymentResponse from(Optional<Payment> optional) {
        if (optional.isEmpty()) {
            return EMPTY;
        }

        Payment payment = optional.get();
        return new PaymentResponse(
                true,
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentKey()
        );
    }
}
