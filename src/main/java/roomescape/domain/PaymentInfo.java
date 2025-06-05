package roomescape.domain;

public record PaymentInfo(String paymentKey, int totalAmount, String orderId) {
}
