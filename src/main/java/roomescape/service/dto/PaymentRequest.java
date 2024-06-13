package roomescape.service.dto;

public record PaymentRequest(String orderId, long amount, String paymentKey) {
}
