package roomescape.payment.global.domain.dto;

import roomescape.payment.global.domain.PgPayment;

public record PgPaymentResponseDto(int amount, String paymentKey, String orderId) {

    public static PgPaymentResponseDto from(PgPayment pgPayment) {
        if (pgPayment == null) {
            return null;
        }
        return new PgPaymentResponseDto(pgPayment.getAmount(), pgPayment.getPaymentKey(), pgPayment.getOrderId());
    }
}
