package roomescape.payment.dto;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId
) {

}
