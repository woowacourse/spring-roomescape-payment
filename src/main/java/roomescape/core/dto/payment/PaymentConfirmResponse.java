package roomescape.core.dto.payment;

public record PaymentConfirmResponse(Long id, Integer totalAmount, String orderId, String paymentKey) {
}
