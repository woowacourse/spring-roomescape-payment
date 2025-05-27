package roomescape.dto;

public record PaymentRequest(int amount, String paymentKey, String orderId) {
}
