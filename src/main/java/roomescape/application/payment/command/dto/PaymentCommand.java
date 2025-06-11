package roomescape.application.payment.command.dto;

public record PaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
