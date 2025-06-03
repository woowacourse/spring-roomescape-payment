package roomescape.application.reservation.command.dto;

import roomescape.application.payment.dto.PaymentCommand;

import java.time.LocalDate;

public record CreateReservationWithPaymentCommand(
        LocalDate date,
        Long timeId,
        Long themeId,
        Long memberId,
        String paymentKey,
        String orderId,
        Long amount,
        String paymentType
) {

    public CreateReservationCommand toCreateWithoutPaymentCommand() {
        return new CreateReservationCommand(date, timeId, themeId, memberId);
    }

    public PaymentCommand getPaymentCommand() {
        return new PaymentCommand(paymentKey, orderId, amount);
    }
}
