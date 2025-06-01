package roomescape.application.payment.command.dto;

public record CreatePaymentCommand(
        String orderId,
        Long amount
) {
}
