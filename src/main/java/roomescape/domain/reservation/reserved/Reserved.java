package roomescape.domain.reservation.reserved;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
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
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@DiscriminatorValue("RESERVED")
public class Reserved extends Reservation {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;

    private Reserved(final User user, final LocalDate date, final TimeSlot timeSlot,
                     final Theme theme) {
        super(date, timeSlot, theme, user);
    }

    public static Reserved register(final User user, final LocalDate date, final TimeSlot timeSlot,
                                    final Theme theme) {

        Reserved reserved = new Reserved(user, date, timeSlot, theme);
        validateNotPastDateTime(date, timeSlot);
        return reserved;
    }

    public static Reserved fromWaiting(final Waiting waiting) {
        return new Reserved(waiting.getUser(), waiting.getDate(), waiting.getTimeSlot(), waiting.getTheme());
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
