package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.service.helper.ReservationItemHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationItemHelper itemHelper;
    private final ReservationItemHelper reservationItemHelper;

    @Transactional
    public ReservationTimeResponse save(final ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        validateUniqueReservationTime(reservationTime);
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(saved);
    }

    private void validateUniqueReservationTime(final ReservationTime reservationTime) {
        final LocalTime startAt = reservationTime.getStartAt();
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 예약 시간 입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> getAll() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeWithAvailabilityResponse> getAllWithAvailabilityBy(long themeId, LocalDate date) {
        List<ReservationTime> availableReservationTime = reservationTimeRepository.findAll();
        return availableReservationTime.stream()
                .map(reservationTime -> {
                            boolean isBooked = itemHelper.isExistReservationItem(date, reservationTime.getId(), themeId);
                            return ReservationTimeWithAvailabilityResponse.from(reservationTime, isBooked);
                        }
                ).toList();
    }

    @Transactional
    public void remove(final long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 예약 시간입니다.");
        }
        if (!reservationTimeRepository.isAvailableToRemove(id)) {
            throw new IllegalArgumentException("[ERROR] 예약이 존재해 예약 시간을 삭제할 수 없습니다.");
        }
        reservationItemHelper.deleteAllReservationItemByTimeId(id);
        reservationTimeRepository.deleteById(id);
    }
}
