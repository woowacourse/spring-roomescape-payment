package roomescape.dto.payment;

public record PaymentResponseDto(
        String paymentKey,
        String orderId,
        Long amount) {
}
