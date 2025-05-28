package roomescape.dto.payment;

public record PaymentConfirmRequest(String orderId, String amount, String paymentKey, String paymentType) {
}
