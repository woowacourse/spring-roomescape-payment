package roomescape.service.payment.dto;

public record PaymentResult(
        long totalAmount,
        String orderId,
        String paymentKey
) {
}
