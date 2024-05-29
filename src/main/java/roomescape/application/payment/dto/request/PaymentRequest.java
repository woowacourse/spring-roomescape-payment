package roomescape.application.payment.dto.request;

public record PaymentRequest(String id, long amount, String paymentKey) {
}
