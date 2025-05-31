package roomescape.payment;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status
) {
}
