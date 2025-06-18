package roomescape.payment.presentation.dto.request;

public record TossPaymentApproveRequest(String paymentKey, String orderId, Long amount, Long reservationId) {
}
