package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional
    public ReservationTimeResponse addReservationTime(final ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        validateUniqueReservationTime(reservationTime);
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(saved);
    }

    @Transactional
    public void removeReservationTime(final long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 테마입니다.");
        }
        if (!reservationTimeRepository.isAvailableToRemove(id)) {
            throw new IllegalArgumentException("[ERROR] 예약이 존재해 테마를 삭제할 수 없습니다.");
        }
        reservationTimeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ReservationTime getReservationTimeById(long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하는 시간이 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> findReservationTimesInfo() {
        return findReservationTimes().stream()
                .map(ReservationTimeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findReservationTimes() {
        return reservationTimeRepository.findAll();
    }

    private void validateUniqueReservationTime(final ReservationTime reservationTime) {
        final LocalTime startAt = reservationTime.getStartAt();
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 예약 시간 입니다.");
        }
    }
}
