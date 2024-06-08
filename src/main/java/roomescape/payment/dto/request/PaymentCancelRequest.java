package roomescape.payment.dto.request;

public record PaymentCancelRequest(String paymentKey, String amount, String cancelReason) {
}
