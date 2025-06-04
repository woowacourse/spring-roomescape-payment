package roomescape.application.payment.dto;

public record PaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {

    public PaymentValidationCommand toValidationCommand() {
        return new PaymentValidationCommand(orderId, amount);
    }
}
