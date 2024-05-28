package roomescape.payment.dto;

public record PaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
