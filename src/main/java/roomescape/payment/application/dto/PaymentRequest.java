package roomescape.payment.application.dto;

public record PaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {
}
