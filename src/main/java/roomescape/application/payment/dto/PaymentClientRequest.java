package roomescape.application.payment.dto;

public record PaymentClientRequest(String orderId, long amount, String paymentKey) {
}
