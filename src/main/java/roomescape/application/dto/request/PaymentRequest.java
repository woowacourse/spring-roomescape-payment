package roomescape.application.dto.request;

public record PaymentRequest(String paymentKey, String orderId, Long amount) {
}
