package roomescape.reservation.dto.request;

public record TossPaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
