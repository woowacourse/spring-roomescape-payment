package roomescape.reservation.dto;

public record PaymentResponse(String status, String paymentKey, String orderId) {
}
