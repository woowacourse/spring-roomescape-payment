package roomescape.application.payment.dto;

public record PaymentClientResponse(String paymentKey, String orderId, String status, long totalAmount) {
}
