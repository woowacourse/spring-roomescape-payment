package roomescape.dto;

public record PaymentRequest(String paymentKey, int amount, String orderId) {
}
