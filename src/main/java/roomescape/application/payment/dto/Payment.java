package roomescape.application.payment.dto;

public record Payment(String paymentKey, String orderId, String status, long totalAmount) {
}
