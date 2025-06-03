package roomescape.dto.response;

public record ConfirmPaymentResponse(
        String paymentKey,
        String orderId,
        int totalAmount
) {
}
