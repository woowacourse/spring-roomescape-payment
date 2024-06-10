package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.repository.WaitingRepository;

@Service
@Transactional(readOnly = true)
public class WaitingSearchService {

    private final WaitingRepository waitingRepository;

    public WaitingSearchService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<Waiting> findAllWaitingReservations() {
        return waitingRepository.findAll();
    }

    public ReservationResponse findReservationWaiting(final Waiting waiting) {
        return ReservationResponse.from(waiting.getReservation());
    }

    public List<Waiting> findPaymentByReservationIds(final List<Long> reservationIds) {
        return waitingRepository.findByReservation_IdIn(reservationIds);
    }
}
