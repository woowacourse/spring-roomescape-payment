package roomescape.reservation.dto;

public record PaymentRequest(String orderId, int amount, String paymentKey) {
}
