package roomescape.domain.reservation.pendingpayment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.BusinessRuleViolationException;
import roomescape.exception.InvalidInputException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class PendingPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected Theme theme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected User user;

    private PendingPayment(final User user, final LocalDate date, final TimeSlot timeSlot, final Theme theme) {
        validateDate(date);
        validateTimeSlot(timeSlot);
        validateNotPastDateTime(date, timeSlot);
        validateTheme(theme);
        validateUser(user);

        this.date = date;
        this.timeSlot = timeSlot;
        this.theme = theme;
        this.user = user;
    }

    public static PendingPayment fromWaiting(final Waiting waiting) {
        return new PendingPayment(waiting.getUser(), waiting.getDate(), waiting.getTimeSlot(), waiting.getTheme());
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

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new InvalidInputException("예약 날짜는 null일 수 없습니다.");
        }
    }

    private void validateTimeSlot(final TimeSlot timeSlot) {
        if (timeSlot == null) {
            throw new InvalidInputException("시간 정보는 null일 수 없습니다.");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new InvalidInputException("테마 정보는 null일 수 없습니다.");
        }
    }

    private void validateUser(final User user) {
        if (user == null) {
            throw new InvalidInputException("사용자 정보는 null일 수 없습니다.");
        }
    }
}
