package roomescape.payment.global.domain.dto;

import roomescape.payment.global.domain.PgPayment;

public record PgPaymentDataDto(String paymentKey, String orderId, int amount, String paymentType) {

    public static PgPaymentDataDto of(PgPayment pgPayment) {
        return new PgPaymentDataDto(pgPayment.getPaymentKey(), pgPayment.getOrderId(), pgPayment.getAmount(), pgPayment.getPaymentType());
    }

    public PgPayment toEntity() {
        return new PgPayment(paymentKey, orderId, amount, paymentType);
    }
}
