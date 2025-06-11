package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.CreatePaymentService;
import roomescape.application.payment.toss.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationWithTossPaymentCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@RequiredArgsConstructor
@Service
public class RegisterReservationWithTossPaymentUseCase {

    private final CreateReservationService createReservationService;
    private final CreatePaymentService createPaymentService;
    private final TossPaymentService tossPaymentService;
    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional
    public ReservationWithPaymentResult execute(final CreateReservationWithTossPaymentCommand command) {
        final Long reservationId = createReservationService.reserve(command.toCreateWithoutPaymentCommand());
        final Long paymentId = createPaymentService.register(PaymentType.TOSS);
        reservationPaymentRepository.save(new ReservationPayment(reservationId, paymentId));
        final Long tossPaymentId = tossPaymentService.save(command.toPaymentCommand(paymentId));
        return new ReservationWithPaymentResult(reservationId, paymentId, tossPaymentId);
    }
}
