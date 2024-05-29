package roomescape.dto;

public record PaymentResponse(
        long id,
        String paymentKey,
        String orderId,
        long totalAmount
) {
}
