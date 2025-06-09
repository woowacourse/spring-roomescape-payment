package roomescape.application.reservation.command.dto;

import roomescape.application.payment.toss.dto.TossPaymentCommand;

import java.time.LocalDate;

public record CreateReservationWithTossPaymentCommand(
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

    public TossPaymentCommand toPaymentCommand(final Long paymentId) {
        return new TossPaymentCommand(paymentId, paymentKey, orderId, amount);
    }
}
