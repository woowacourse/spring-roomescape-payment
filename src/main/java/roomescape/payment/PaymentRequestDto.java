package roomescape.payment;

public record PaymentRequestDto(String paymentKey, String orderId, int amount) {
}
