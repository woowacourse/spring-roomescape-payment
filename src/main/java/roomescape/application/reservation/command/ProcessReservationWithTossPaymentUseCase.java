package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;

@Service
@RequiredArgsConstructor
public class ProcessReservationWithTossPaymentUseCase {

    private final RegisterReservationWithTossPaymentUseCase registerReservationWithTossPaymentUseCase;
    private final TossPaymentService tossPaymentService;

    public Long execute(final CreateReservationWithPaymentCommand command) {
        final ReservationWithPaymentResult result = registerReservationWithTossPaymentUseCase.execute(command);
        tossPaymentService.approve(command.toPaymentCommand(), result.paymentId());

        return result.reservationId();
    }
}
