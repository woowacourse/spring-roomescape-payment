package roomescape.dto.request;

public record PaymentRequest(int amount, String paymentKey, String orderId) {
}
