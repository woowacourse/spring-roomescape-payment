package roomescape.payment.dto.request;

public record PaymentRequest(String paymentKey, String orderId, String amount, String paymentType) {
}
