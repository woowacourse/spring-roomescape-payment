package roomescape.payment.presentation.dto.response;

public record TossPaymentApproveResponse(String paymentKey, String orderId, Long totalAmount) {
}
