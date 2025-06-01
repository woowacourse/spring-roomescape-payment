package roomescape.dto.response;

public record TossPaymentConfirmResponseDto(
        String status,
        String paymentKey,
        String orderId
) {
}
