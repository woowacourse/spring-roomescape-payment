package roomescape.client.dto.request;

public record TossPaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
