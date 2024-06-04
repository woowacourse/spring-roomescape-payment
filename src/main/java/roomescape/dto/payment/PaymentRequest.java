package roomescape.dto.payment;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
}
