package roomescape.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeManager {

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDateTime todayCurrentTime() {
        return LocalDateTime.now();
    }

}
