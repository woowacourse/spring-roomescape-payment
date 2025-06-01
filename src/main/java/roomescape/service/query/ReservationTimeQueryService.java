package roomescape.service.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.dto.business.ReservationTimeWithBookState;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationTimeQueryService {

    private final ReservationTimeRepository timeRepository;

    public ReservationTimeQueryService(ReservationTimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public List<ReservationTimeResponse> findAllReservationTimes() {
        List<ReservationTime> reservationTimes = timeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::new)
                .toList();
    }

    public List<ReservationTimeWithBookState> findReservationTimesWithBooking(long themeId, LocalDate date) {
        List<ReservationTime> allTimes = timeRepository.findAllOrderByStartAt();
        List<ReservationTime> bookedTimes = timeRepository.findReservationTimesWithBooking(themeId, date);
        return calculateAllTimesWithBookedState(allTimes, bookedTimes);
    }

    public ReservationTime getTimeById(long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 예약시간이 존재하지 않습니다."));
    }


    public ReservationTime getReservationTimeById(Long reservationTimeId) {
        return timeRepository.findById(reservationTimeId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 예약시간은 존재하지 않습니다."));
    }

    private List<ReservationTimeWithBookState> calculateAllTimesWithBookedState(
            List<ReservationTime> allTimes,
            List<ReservationTime> bookedTimes
    ) {
        List<ReservationTimeWithBookState> allTimesWithBookState = new ArrayList<>();
        for (ReservationTime time : allTimes) {
            if (bookedTimes.contains(time)) {
                allTimesWithBookState.add(new ReservationTimeWithBookState(time.getId(), time.getStartAt(), true));
                continue;
            }
            allTimesWithBookState.add(new ReservationTimeWithBookState(time.getId(), time.getStartAt(), false));
        }
        return allTimesWithBookState;
    }
}
