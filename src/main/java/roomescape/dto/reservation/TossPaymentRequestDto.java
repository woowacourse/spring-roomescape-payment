package roomescape.dto.reservation;

public record TossPaymentRequestDto(
        String paymentKey,
        String orderId
) {
}
