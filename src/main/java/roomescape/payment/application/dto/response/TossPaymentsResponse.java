package roomescape.payment.application.dto.response;

import roomescape.payment.model.entity.Payment;

public record TossPaymentsResponse(
        String requestedAt,
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status,
        Long reservationId
) {
    public Payment toEntity(final Long reservationId) {
        return Payment.of(
                this.requestedAt,
                this.paymentKey,
                this.totalAmount,
                reservationId
        );
    }

}
