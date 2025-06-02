package roomescape.service.reservation;

import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.ALREADY_EXIST_RESERVATION_TIME;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.CANNOT_DELETE_TIME_WITH_RESERVATIONS;

import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.global.exception.roomescape.RoomEscapeErrorStatus;
import roomescape.global.exception.roomescape.RoomEscapeException;

@RequiredArgsConstructor
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeResponse addReservationTime(final ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        validateUniqueReservationTime(reservationTime);
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(saved);
    }

    public void removeReservationTime(final long id) {
        final ReservationTime reservationTime = getReservationTimeById(id);
        try {
            reservationTimeRepository.deleteById(reservationTime.getId());
        } catch (DataIntegrityViolationException e) {
            throw new RoomEscapeException(CANNOT_DELETE_TIME_WITH_RESERVATIONS);
        }
    }

    public ReservationTime getReservationTimeById(long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(RoomEscapeErrorStatus.NON_EXIST_RESERVATION_TIME));
    }

    public List<ReservationTimeResponse> findReservationTimesInfo() {
        return findReservationTimes().stream()
                .map(ReservationTimeResponse::from).toList();
    }

    public List<ReservationTime> findReservationTimes() {
        return reservationTimeRepository.findAll();
    }

    private void validateUniqueReservationTime(final ReservationTime reservationTime) {
        final LocalTime startAt = reservationTime.getStartAt();
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new RoomEscapeException(ALREADY_EXIST_RESERVATION_TIME);
        }
    }
}
