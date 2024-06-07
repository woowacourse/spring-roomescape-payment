package roomescape.dto.request.payment;

public record PaymentRequest(String orderId, int amount, String paymentKey) {
}
