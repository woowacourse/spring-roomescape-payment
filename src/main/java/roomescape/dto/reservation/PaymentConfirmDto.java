package roomescape.dto.reservation;

public record PaymentConfirmDto(
        String paymentKey,
        String orderId,
        Long amount
) {

}
