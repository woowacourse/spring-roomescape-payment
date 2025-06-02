package roomescape.presentation.dto.request;

public record PaymentProcessRequest(String paymentKey, String orderId, String amount) {
}
