package roomescape.service.reservation;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.schedule.ReservationDate;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationResponse> findByCondition(ReservationFilterRequest reservationFilterRequest) {
        ReservationDate dateFrom = ReservationDate.of(reservationFilterRequest.dateFrom());
        ReservationDate dateTo = ReservationDate.of(reservationFilterRequest.dateTo());
        return reservationRepository.findBy(reservationFilterRequest.memberId(), reservationFilterRequest.themeId(),
            dateFrom, dateTo).stream().map(ReservationResponse::new).toList();
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(ReservationStatus.RESERVED).stream()
            .map(ReservationResponse::new)
            .toList();
    }
}
