package roomescape.dto.payment;

public record PaymentConfirmRequest(String orderId, long amount, String paymentKey, String paymentType) {
}
