package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@Service
@RequiredArgsConstructor
public class ProcessReservationByAdminUseCase {

    private final CreateReservationService createReservationService;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public Long execute(final CreateReservationCommand command, final Long adminId) {
        final Long reservationId = createReservationService.reserve(command);
        reservationPaymentRepository.save(new ReservationPayment(reservationId, PaymentType.ADMIN, adminId));

        return reservationId;
    }
}
