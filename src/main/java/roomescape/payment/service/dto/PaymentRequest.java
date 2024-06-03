package roomescape.payment.service.dto;

public record PaymentRequest(
        long amount,
        String orderId,
        String paymentKey
) {
}
