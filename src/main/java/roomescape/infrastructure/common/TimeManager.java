package roomescape.infrastructure.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import roomescape.domain.reservation.ReservationTimeManager;
import roomescape.domain.theme.ThemeTimeManager;

@Component
public class TimeManager implements ThemeTimeManager, ReservationTimeManager {

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDateTime todayCurrentTime() {
        return LocalDateTime.now();
    }

}
