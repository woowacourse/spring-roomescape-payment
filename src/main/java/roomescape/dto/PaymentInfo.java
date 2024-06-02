package roomescape.dto;

public record PaymentInfo(Long amount, String orderId, String paymentKey) {
}
