package roomescape.fixture;

import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.waiting.Waiting;

public class WaitingFixture {

    public static Waiting CREATE_WAITING_OF(Long id, User user, LocalDate date, TimeSlot timeSlot, Theme theme) {
        Waiting waiting = Waiting.register(user, date, timeSlot, theme);
        ReflectionTestUtils.setField(waiting, "id", id);
        return waiting;
    }
}

