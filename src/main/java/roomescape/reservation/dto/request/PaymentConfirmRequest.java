package roomescape.reservation.dto.request;

public record PaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
