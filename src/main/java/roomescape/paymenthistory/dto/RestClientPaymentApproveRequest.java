package roomescape.paymenthistory.dto;

// TODO: 도메인으로 분리할지 고려해보기
public record RestClientPaymentApproveRequest(String paymentKey, String orderId, int amount) {
}
