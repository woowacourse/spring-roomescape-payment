package roomescape.schedule.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.repository.ReservationScheduleRepository;
import roomescape.theme.domain.DateRange;

@Service
@Transactional(readOnly = true)
public class ScheduleQueryService {
    private final ReservationScheduleRepository reservationScheduleRepository;

    public ScheduleQueryService(final ReservationScheduleRepository reservationScheduleRepository) {
        this.reservationScheduleRepository = reservationScheduleRepository;
    }

    public ReservationSchedule getSchedule(Long timeId, Long themeId, ReservationDate date) {
        return reservationScheduleRepository.findByReservationTime_IdAndTheme_IdAndReservationDate_Date(
                timeId, themeId, date.date()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 일정입니다."));
    }

    public Set<LocalDate> existingScheduledDates(DateRange dateRange) {
        return reservationScheduleRepository.findSchedulesBetweenDates(dateRange.getStartDate(), dateRange.getEndDate())
                .stream()
                .map(ReservationSchedule::getDate)
                .collect(Collectors.toSet());
    }
}
