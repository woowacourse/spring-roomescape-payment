package roomescape.infrastructure.payment.dto;

public record PaymentApproveRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
