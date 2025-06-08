package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@RequiredArgsConstructor
@Service
public class RegisterReservationWithTossPaymentUseCase {

    private final CreateReservationService createReservationService;
    private final TossPaymentService paymentService;
    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional
    public ReservationWithPaymentResult execute(final CreateReservationWithPaymentCommand command) {
        final Long reservationId = createReservationService.reserve(command.toCreateWithoutPaymentCommand());
        final Long paymentId = paymentService.save(command.toPaymentCommand());
        reservationPaymentRepository.save(new ReservationPayment(reservationId, PaymentType.TOSS, paymentId));
        return new ReservationWithPaymentResult(reservationId, paymentId);
    }
}
