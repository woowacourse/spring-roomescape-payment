package roomescape.dto.reservation;

public record TossPaymentConfirmRequestDto(
        String paymentKey,
        String orderId,
        Long amount
) {

}
