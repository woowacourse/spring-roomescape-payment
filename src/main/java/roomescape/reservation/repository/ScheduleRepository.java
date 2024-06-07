package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.reservation.domain.Schedule;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public interface ScheduleRepository extends ListCrudRepository<Schedule, Long> {
    Optional<Schedule> findByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);
}
