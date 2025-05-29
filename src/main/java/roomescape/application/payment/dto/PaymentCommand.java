package roomescape.application.payment.dto;

public record PaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {

}
