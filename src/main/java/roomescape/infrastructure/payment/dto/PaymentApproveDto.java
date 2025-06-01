package roomescape.infrastructure.payment.dto;

public record PaymentApproveDto(
        String paymentKey,
        String orderId,
        Long amount
) {
}
