package roomescape.dto.response;

public record PaymentRequest(String orderId, int amount, String paymentKey) {
}
