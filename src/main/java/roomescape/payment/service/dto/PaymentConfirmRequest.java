package roomescape.payment.service.dto;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
