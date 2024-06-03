package roomescape.application.payment.dto.request;

public record PaymentRequest(String orderId, long amount, String paymentKey) {
}
