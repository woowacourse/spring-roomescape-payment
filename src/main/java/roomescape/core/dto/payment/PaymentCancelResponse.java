package roomescape.core.dto.payment;

public record PaymentCancelResponse(Long id, Integer totalAmount, String orderId, String paymentKey) {
}
