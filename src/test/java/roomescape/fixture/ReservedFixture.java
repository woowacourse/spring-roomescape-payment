package roomescape.fixture;

import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

public class ReservedFixture {

    public static Reserved CREATE_RESERVED_OF(Long id, User user, LocalDate date, TimeSlot timeSlot, Theme theme,
                                              Payment payment) {
        Reserved register = Reserved.register(user, date, timeSlot, theme);
        ReflectionTestUtils.setField(register, "id", id);
        register.registerPayment(payment);
        return register;
    }

    public static Reserved CREATE_RESERVED_OF(Long id, User user, LocalDate date, TimeSlot timeSlot, Theme theme) {
        return CREATE_RESERVED_OF(id, user, date, timeSlot, theme, null);
    }
}
