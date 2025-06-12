package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Accessors(fluent = true)
@ToString
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne
    private User user;
    private LocalDate date;
    @ManyToOne
    private TimeSlot timeSlot;
    @ManyToOne
    private Theme theme;
    @OneToOne
    private Payment payment;

    private Reservation(final Long id,
                        final User user,
                        final LocalDate date,
                        final TimeSlot timeSlot,
                        final Theme theme,
                        final Payment payment) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.timeSlot = timeSlot;
        this.theme = theme;
        this.payment = payment;
    }

    protected Reservation() {
    }

    public static Reservation registerWithAdminPrivileges(final User user,
                                                          final LocalDate date,
                                                          final TimeSlot timeSlot,
                                                          final Theme theme) {
        validateNotPastDateTime(date, timeSlot);
        return new Reservation(null, user, date, timeSlot, theme, null);
    }

    public static Reservation registerWithUserPrivileges(final User user,
                                                         final LocalDate date,
                                                         final TimeSlot timeSlot,
                                                         final Theme theme,
                                                         final Payment payment) {
        validateNotPastDateTime(date, timeSlot);
        return new Reservation(null, user, date, timeSlot, theme, payment);
    }

    public static Reservation fromWaiting(final Waiting waiting) {
        return new Reservation(
                null,
                waiting.user(),
                waiting.date(),
                waiting.timeSlot(),
                waiting.theme(),
                null
        );
    }

    private static void validateNotPastDateTime(final LocalDate date, final TimeSlot timeSlot) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        boolean isPastDate = date.isBefore(currentDate);
        boolean isCurrentDateAndPastTime = date.isEqual(currentDate) && timeSlot.isTimeBefore(currentTime);

        if (isPastDate || isCurrentDateAndPastTime) {
            throw new BusinessRuleViolationException("이전 날짜로 예약할 수 없습니다.");
        }
    }
}

