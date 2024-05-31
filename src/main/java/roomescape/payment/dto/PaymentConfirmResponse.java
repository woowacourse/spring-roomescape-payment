package roomescape.payment.dto;

public record PaymentConfirmResponse(
        int totalAmount,
        String orderId,
        String paymentKey) {
}
