package roomescape.service.payment.dto;

public record PaymentResult(
        int totalAmount,
        String orderId,
        String paymentKey
) {
}
