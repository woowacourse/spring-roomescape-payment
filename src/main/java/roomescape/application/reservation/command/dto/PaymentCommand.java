package roomescape.application.reservation.command.dto;

public record PaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
