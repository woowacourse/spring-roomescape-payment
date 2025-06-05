package roomescape.external.tosspayment.dto;

public record PaymentConfirmRequest(
        String orderId,
        Long amount,
        String paymentKey
) {
}
