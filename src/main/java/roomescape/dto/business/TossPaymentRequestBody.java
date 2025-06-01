package roomescape.dto.business;

public record TossPaymentRequestBody(String paymentKey, String orderId, long amount) {
}
