package roomescape.schedule.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.repository.ReservationScheduleRepository;
import roomescape.theme.domain.DateRange;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeQueryService;
import roomescape.time.domain.ReservationTime;
import roomescape.time.service.TimeQueryService;

@Service
@Transactional(readOnly = true)
public class ScheduleCommandService {

    private final ReservationScheduleRepository reservationScheduleRepository;
    private final ScheduleQueryService scheduleQueryService;
    private final ThemeQueryService themeQueryService;
    private final TimeQueryService timeQueryService;
    private final Clock clock;

    public ScheduleCommandService(
            final ReservationScheduleRepository reservationScheduleRepository,
            final ScheduleQueryService scheduleQueryService,
            final ThemeQueryService themeQueryService,
            final TimeQueryService timeQueryService,
            final Clock clock
    ) {
        this.reservationScheduleRepository = reservationScheduleRepository;
        this.scheduleQueryService = scheduleQueryService;
        this.themeQueryService = themeQueryService;
        this.timeQueryService = timeQueryService;
        this.clock = clock;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createSchedule();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createSchedule() {
        DateRange nextTwoMonthsRange = DateRange.createNextTwoMonthsRange(clock);
        Set<LocalDate> scheduledDates = scheduleQueryService.existingScheduledDates(nextTwoMonthsRange);
        List<ReservationSchedule> newSchedules = generateNewSchedules(scheduledDates, nextTwoMonthsRange);
        reservationScheduleRepository.saveAll(newSchedules);
    }


    private List<ReservationSchedule> generateNewSchedules(final Set<LocalDate> scheduledDates, final DateRange range) {
        List<ReservationTime> times = timeQueryService.findAll();
        List<Theme> themes = themeQueryService.findAll();
        Set<LocalDate> nonContainsSchedule = range.differenceTo(scheduledDates);
        return nonContainsSchedule.stream()
                .flatMap(date -> themeTimeCombinations(date, times, themes).stream())
                .toList();
    }

    private List<ReservationSchedule> themeTimeCombinations(
            final LocalDate date,
            final List<ReservationTime> times,
            final List<Theme> themes
    ) {
        return themes.stream()
                .flatMap(theme -> times.stream()
                        .map(time -> new ReservationSchedule(null, new ReservationDate(date), time, theme)))
                .toList();
    }
}
