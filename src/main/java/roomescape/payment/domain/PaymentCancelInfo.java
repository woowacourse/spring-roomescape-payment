package roomescape.payment.domain;

public record PaymentCancelInfo(String paymentKey, String cancelReason) {
}
