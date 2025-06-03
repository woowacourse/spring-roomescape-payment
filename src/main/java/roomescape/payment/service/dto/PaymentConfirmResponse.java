package roomescape.payment.service.dto;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status
) {
}
