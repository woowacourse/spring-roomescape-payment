package roomescape.domain.reservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.common.BaseReservation;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class Reservation extends BaseReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;

    private Reservation(final Long id, final User user, final LocalDate date, final TimeSlot timeSlot,
                        final Theme theme) {
        super(date, timeSlot, theme, user);
        this.id = id;
    }

    public static Reservation register(final User user, final LocalDate date, final TimeSlot timeSlot,
                                       final Theme theme) {

        Reservation reservation = new Reservation(null, user, date, timeSlot, theme);
        validateNotPastDateTime(date, timeSlot);
        return reservation;
    }

    public static Reservation fromWaiting(final Waiting waiting) {
        return new Reservation(null, waiting.getUser(), waiting.getDate(), waiting.getTimeSlot(), waiting.getTheme());
    }

    public void registerPayment(Payment payment) {
        this.payment = payment;
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
