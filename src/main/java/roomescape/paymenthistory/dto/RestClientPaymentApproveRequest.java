package roomescape.paymenthistory.dto;

public record RestClientPaymentApproveRequest(String paymentKey, String orderId, int amount) {
}
