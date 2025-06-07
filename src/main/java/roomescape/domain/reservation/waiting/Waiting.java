package roomescape.domain.reservation.waiting;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@DiscriminatorValue("WAITING")
public class Waiting extends Reservation {

    private Waiting(
            final User user,
            final LocalDate date,
            final TimeSlot timeSlot,
            final Theme theme
    ) {
        super(date, timeSlot, theme, user);
    }

    public static Waiting register(
            final User user,
            final LocalDate date,
            final TimeSlot timeSlot,
            final Theme theme
    ) {

        return new Waiting(user, date, timeSlot, theme);
    }
}
