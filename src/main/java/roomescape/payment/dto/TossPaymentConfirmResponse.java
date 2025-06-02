package roomescape.payment.dto;

public record TossPaymentConfirmResponse(String orderId, String paymentKey)
        implements PaymentConfirmResponse {
}