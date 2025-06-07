package roomescape.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.facade.dto.ReservationWithPaymentResult;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.CreateReservationService;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@Service
@RequiredArgsConstructor
public class ReservationTossPaymentRegister {

    private final CreateReservationService createReservationService;
    private final TossPaymentService paymentService;
    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional
    public ReservationWithPaymentResult createReservationAndPendingPayment(final CreateReservationWithPaymentCommand command) {
        final Long reservationId = createReservationService.reserve(command.toCreateWithoutPaymentCommand());
        final Long paymentId = paymentService.save(command.toPaymentCommand());
        reservationPaymentRepository.save(new ReservationPayment(reservationId, PaymentType.TOSS, paymentId));
        return new ReservationWithPaymentResult(reservationId, paymentId);
    }
}
