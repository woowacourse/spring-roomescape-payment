package roomescape.external.tosspayment.dto;

public record TossPaymentConfirmCommand(
        String orderId,
        Long amount,
        String paymentKey
) {
}
