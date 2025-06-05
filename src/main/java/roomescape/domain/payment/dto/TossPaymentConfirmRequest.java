package roomescape.domain.payment.dto;

public record TossPaymentConfirmRequest(int amount, String orderId, String paymentKey) implements
        PaymentConfirmRequest {
}
