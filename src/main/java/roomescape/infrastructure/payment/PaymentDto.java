package roomescape.infrastructure.payment;

public record PaymentDto(String orderId, String paymentKey, int amount) {
}
