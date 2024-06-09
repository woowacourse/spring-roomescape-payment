package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.repository.ReservationRepository;
import roomescape.service.reservation.module.ReservationValidator;

@Service
@Transactional
public class WaitingApproveService {

    private final ReservationValidator reservationValidator;
    private final ReservationRepository reservationRepository;

    public WaitingApproveService(ReservationValidator reservationValidator,
                                 ReservationRepository reservationRepository
    ) {
        this.reservationValidator = reservationValidator;
        this.reservationRepository = reservationRepository;
    }

    public void approveWaitingReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        reservationValidator.validateApproval(reservation);
        reservation.changeStatusToPaymentPending();
    }
}
