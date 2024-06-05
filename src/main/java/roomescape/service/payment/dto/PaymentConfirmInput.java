package roomescape.service.payment.dto;

public record PaymentConfirmInput(String orderId, Integer amount, String paymentKey) {
}
