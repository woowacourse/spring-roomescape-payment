package roomescape.dto.reservation;

public record TossPaymentConfirmRequestDto(
        String orderId,
        String paymentKey,
        Long amount
) {
}
