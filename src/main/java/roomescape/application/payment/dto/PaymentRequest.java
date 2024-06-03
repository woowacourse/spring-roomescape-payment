package roomescape.application.payment.dto;

public record PaymentRequest(String orderId, long amount, String paymentKey) {
}
