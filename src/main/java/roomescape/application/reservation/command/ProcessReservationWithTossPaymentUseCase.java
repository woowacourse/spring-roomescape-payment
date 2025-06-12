package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationWithTossPaymentCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;

@Service
@RequiredArgsConstructor
public class ProcessReservationWithTossPaymentUseCase {

    private final RegisterReservationWithTossPaymentUseCase registerReservationWithTossPaymentUseCase;
    private final TossPaymentService tossPaymentService;

    public Long execute(final CreateReservationWithTossPaymentCommand command) {
        final ReservationWithPaymentResult result = registerReservationWithTossPaymentUseCase.execute(command);
        tossPaymentService.approve(command.toPaymentCommand(result.paymentId()), result.detailPaymentId());

        return result.reservationId();
    }
}
