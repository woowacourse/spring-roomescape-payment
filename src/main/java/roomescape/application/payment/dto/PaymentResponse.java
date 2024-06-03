package roomescape.application.payment.dto;

public record PaymentResponse(String paymentKey, String orderId, String status, long totalAmount) {
}
