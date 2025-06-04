package roomescape.fixture;

import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

public class PendingPaymentFixture {

    public static PendingPayment CREATE_PENDING_PAYMENT_OF(Long id, User user, LocalDate date, TimeSlot timeSlot,
                                                           Theme theme) {
        PendingPayment pendingPayment = PendingPayment.fromWaiting(Waiting.register(user, date, timeSlot, theme));
        ReflectionTestUtils.setField(pendingPayment, "id", id);
        return pendingPayment;
    }
}
