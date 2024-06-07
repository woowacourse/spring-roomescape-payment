package roomescape.service.payment.dto;

public record PaymentConfirmInput(String orderId, Long amount, String paymentKey) {
}
