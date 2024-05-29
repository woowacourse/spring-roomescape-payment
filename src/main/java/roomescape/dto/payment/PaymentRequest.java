package roomescape.dto.payment;

public record PaymentRequest(String orderId, int amount, String paymentKey) {
}
