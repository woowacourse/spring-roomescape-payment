package roomescape.payment.dto.request;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
