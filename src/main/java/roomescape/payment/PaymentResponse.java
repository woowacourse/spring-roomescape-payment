package roomescape.payment;

public record PaymentResponse(String paymentKey, String orderId, Long totalAmount) {
}
