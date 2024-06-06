package roomescape.service.payment.dto;

public record PrePaymentRequest(String orderId, long amount) {
}
