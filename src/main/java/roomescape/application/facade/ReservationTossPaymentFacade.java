package roomescape.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.facade.dto.ReservationWithPaymentResult;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;

@Service
@RequiredArgsConstructor
public class ReservationTossPaymentFacade {

    private final ReservationTossPaymentRegister reservationTossPaymentRegister;
    private final TossPaymentService paymentService;

    public Long reserveWithPayment(final CreateReservationWithPaymentCommand command) {
        final ReservationWithPaymentResult result =
                reservationTossPaymentRegister.createReservationAndPendingPayment(command);

        paymentService.approve(command.toPaymentCommand(), result.paymentId());

        return result.reservationId();
    }
}
