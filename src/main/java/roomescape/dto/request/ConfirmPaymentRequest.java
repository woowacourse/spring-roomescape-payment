package roomescape.dto.request;

public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
