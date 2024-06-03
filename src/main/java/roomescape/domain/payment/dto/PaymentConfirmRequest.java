package roomescape.domain.payment.dto;

public record PaymentConfirmRequest(String orderId, Long amount, String paymentKey) {
}
