package roomescape.payment.dto;

public record PaymentRequest(String paymentKey, String orderId, long amount) {
}
