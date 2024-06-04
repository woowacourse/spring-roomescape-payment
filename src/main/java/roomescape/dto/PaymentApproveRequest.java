package roomescape.dto;

public record PaymentApproveRequest(String paymentKey, String orderId, long amount, long reservationId) {
}
