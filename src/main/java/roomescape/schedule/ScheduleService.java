package roomescape.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.custom.reason.schedule.ScheduleConflictException;
import roomescape.exception.custom.reason.schedule.ScheduleNotExistException;
import roomescape.reservationtime.ReservationTime;
import roomescape.reservationtime.ReservationTimeService;
import roomescape.schedule.dto.ScheduleRequest;
import roomescape.schedule.dto.ScheduleResponse;
import roomescape.theme.Theme;
import roomescape.theme.ThemeService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    @Transactional
    public ScheduleResponse create(ScheduleRequest request) {
        ReservationTime reservationTime = reservationTimeService.getById(request.reservationTimeId());
        Theme theme = themeService.getById(request.themeId());
        validateDuplication(reservationTime, theme, request.date());

        Schedule schedule = new Schedule(request.date(), reservationTime, theme);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("EVENT: SCHEDULE_CREATED, id={}. date={}, time={}, themeName={}",
                savedSchedule.getId(),
                savedSchedule.getDate(),
                savedSchedule.getReservationTime().getStartAt(),
                savedSchedule.getTheme().getName());
        return ScheduleResponse.of(savedSchedule);
    }

    @Transactional(readOnly = true)
    public Schedule getByDateAndTimeIdAndThemeId(final LocalDate date, final Long timeId, final Long themeId) {
        return scheduleRepository.findByDateAndReservationTime_IdAndTheme_Id(date, timeId, themeId)
                .orElseThrow(ScheduleNotExistException::new);
    }

    private void validateDuplication(final ReservationTime reservationTime, final Theme theme, final LocalDate date) {
        if (scheduleRepository.existsByReservationTimeAndThemeAndDate(reservationTime, theme, date)) {
            log.warn("EVENT: SCHEDULE_CREATE_FAILED - DUPLICATED, themeName={}, date={}, time={}",
                    theme.getName(),
                    date,
                    reservationTime.getStartAt());
            throw new ScheduleConflictException();
        }
    }
}
