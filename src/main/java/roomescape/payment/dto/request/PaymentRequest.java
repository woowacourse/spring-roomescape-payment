package roomescape.payment.dto.request;

public record PaymentRequest(String paymentKey, String orderId, Long amount, String paymentType) {
}
