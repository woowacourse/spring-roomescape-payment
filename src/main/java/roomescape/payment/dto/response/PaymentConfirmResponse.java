package roomescape.payment.dto.response;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String type
) {
}
