package roomescape.reservation.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(
        long id,
        String paymentKey,
        String orderId,
        long amount
) {

    public PaymentResponse(final Payment payment) {
        this(payment.getId(),
                payment.getPaymentKey().getValue(),
                payment.getOrderId().getValue(),
                payment.getAmount().getValue()
        );
    }
}
