package roomescape.integration.fixture;

import org.springframework.stereotype.Component;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.repository.ReservationScheduleRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Component
public class ReservationScheduleDbFixture {

    private ReservationScheduleRepository reservationScheduleRepository;

    public ReservationScheduleDbFixture(final ReservationScheduleRepository reservationScheduleRepository) {
        this.reservationScheduleRepository = reservationScheduleRepository;
    }

    public ReservationSchedule 예약_일정_25_4_22(
            final ReservationTime time,
            final Theme theme
    ) {
        ReservationDate date = ReservationDateFixture.예약날짜_25_4_22;
        return createSchedule(date, time, theme);
    }

    public ReservationSchedule 예약_일정_오늘(
            final ReservationTime time,
            final Theme theme
    ) {
        ReservationDate date = ReservationDateFixture.예약날짜_오늘;
        return createSchedule(date, time, theme);
    }

    public ReservationSchedule createSchedule(
            final ReservationDate reservationDate,
            final ReservationTime reservationTime,
            final Theme theme
    ) {
        return reservationScheduleRepository.save(new ReservationSchedule(
                null,
                reservationDate,
                reservationTime,
                theme
        ));
    }
}
