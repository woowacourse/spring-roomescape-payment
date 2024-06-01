package roomescape.service.request;

public record PaymentApproveDto(String paymentKey, String orderId, Long amount) {
}
