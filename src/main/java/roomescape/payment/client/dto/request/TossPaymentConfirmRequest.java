package roomescape.payment.client.dto.request;

public record TossPaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
