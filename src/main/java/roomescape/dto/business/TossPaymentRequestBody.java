package roomescape.dto.business;

public record TossPaymentRequestBody(String paymentKey, String orderId, String paymentType, long amount) {
}
