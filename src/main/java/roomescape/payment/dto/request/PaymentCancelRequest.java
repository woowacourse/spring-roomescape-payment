package roomescape.payment.dto.request;

public record PaymentCancelRequest(String paymentKey, Long amount, String cancelReason) {
}
