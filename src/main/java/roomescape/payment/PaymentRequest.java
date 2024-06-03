package roomescape.payment;

public record PaymentRequest(String paymentKey, String orderId, String amount, String paymentType) {
}
