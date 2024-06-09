package roomescape.payment.domain;

public record NewPayment(
        String paymentKey,
        String orderId,
        Long amount,
        String paymentType
) {
}
