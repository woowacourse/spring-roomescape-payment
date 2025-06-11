package roomescape.service.dto;

public record PaymentConfirmDto(
        String paymentKey,
        String orderId,
        Long amount
) {

}
