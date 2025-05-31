package roomescape.dto.reservation;

public record PaymentConfirmRequestDto(
        String paymentKey,
        String orderId,
        Long amount
) {

}
