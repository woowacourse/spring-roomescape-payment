package roomescape.infrastructure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import roomescape.reservation.ReservationTimeManager;
import roomescape.theme.ThemeTimeManager;

@Component
public class TimeManager implements ThemeTimeManager, ReservationTimeManager {

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDateTime todayCurrentTime() {
        return LocalDateTime.now();
    }

}
