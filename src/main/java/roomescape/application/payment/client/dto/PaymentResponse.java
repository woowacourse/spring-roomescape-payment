package roomescape.application.payment.client.dto;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        long totalAmount,
        String type,
        String status
) {
}
