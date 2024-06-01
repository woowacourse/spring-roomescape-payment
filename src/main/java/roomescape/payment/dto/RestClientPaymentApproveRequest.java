package roomescape.payment.dto;

public record RestClientPaymentApproveRequest(String paymentKey, String orderId, int amount) {
}
