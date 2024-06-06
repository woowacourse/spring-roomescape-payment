package roomescape.application.dto.request.payment;

public record PaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {
}
