package roomescape.payment.dto;

public record TossPaymentConfirmRequest(int amount, String orderId, String paymentKey) implements
        PaymentConfirmRequest {
}
