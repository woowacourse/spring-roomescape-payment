package roomescape.payment.dto;

public record TossPaymentConfirmCommand(
        String orderId,
        Long amount,
        String paymentKey
) {
}
