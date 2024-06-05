package roomescape.reservation.dto;

public record PaymentRequest(String paymentKey, String orderId, Long amount) {
}
