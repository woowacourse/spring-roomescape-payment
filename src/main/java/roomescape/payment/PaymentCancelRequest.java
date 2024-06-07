package roomescape.payment;

public record PaymentCancelRequest(String paymentKey, String amount, String cancelReason) {
}
