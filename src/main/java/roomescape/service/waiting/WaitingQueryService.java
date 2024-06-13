package roomescape.service.waiting;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.service.reservation.dto.ReservationResponse;

@Service
@Transactional(readOnly = true)
public class WaitingQueryService {

    private final ReservationRepository reservationRepository;

    public WaitingQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(ReservationStatus.WAITING).stream()
            .map(ReservationResponse::new)
            .toList();
    }
}
