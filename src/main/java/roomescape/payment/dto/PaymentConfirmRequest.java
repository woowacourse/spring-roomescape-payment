package roomescape.payment.dto;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
