package roomescape.application.dto.request.payment;

public record PaymentRequest(
        int amount,
        String orderId,
        String paymentKey
) {
}
