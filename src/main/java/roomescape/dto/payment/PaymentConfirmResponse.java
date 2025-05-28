package roomescape.dto.payment;

public record PaymentConfirmResponse(String orderId, String paymentKey, Long totalAmount, String status) {
}
