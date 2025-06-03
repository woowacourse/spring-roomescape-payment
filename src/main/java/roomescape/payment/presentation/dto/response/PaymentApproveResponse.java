package roomescape.payment.presentation.dto.response;

public record PaymentApproveResponse(String paymentKey, String orderId, Long totalAmount) {
}
