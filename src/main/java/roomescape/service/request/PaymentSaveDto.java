package roomescape.service.request;

public record PaymentSaveDto(Long memberId, Long reservationId, String paymentKey, String orderId, Long amount) {
}
