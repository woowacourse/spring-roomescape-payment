package roomescape.domain.reservation.pendingpayment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@DiscriminatorValue("PENDING_PAYMENT")
public class PendingPayment extends Reservation {

    private PendingPayment(final User user, final LocalDate date, final TimeSlot timeSlot, final Theme theme) {
        super(date, timeSlot, theme, user);
    }

    public static PendingPayment fromWaiting(final Waiting waiting) {
        return new PendingPayment(waiting.getUser(), waiting.getDate(), waiting.getTimeSlot(), waiting.getTheme());
    }
}
