package roomescape.dto.payment;

public record PaymentConfirmResponse(String paymentKey, String orderId, long totalAmount) {
}
