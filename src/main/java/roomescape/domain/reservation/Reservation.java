package roomescape.domain.reservation;

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
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme;

    private Reservation(final Long id,
                        final User user,
                        final LocalDate date,
                        final TimeSlot timeSlot,
                        final Theme theme) {

        validateUser(user);
        validateDate(date);
        validateTimeSlot(timeSlot);
        validateTheme(theme);

        this.id = id;
        this.user = user;
        this.date = date;
        this.timeSlot = timeSlot;
        this.theme = theme;
    }

    public static Reservation register(final User user,
                                       final LocalDate date,
                                       final TimeSlot timeSlot,
                                       final Theme theme) {

        Reservation reservation = new Reservation(null, user, date, timeSlot, theme);
        validateNotPastDateTime(date, timeSlot);
        return reservation;
    }

    public static Reservation fromWaiting(final Waiting waiting) {
        return new Reservation(null, waiting.getUser(), waiting.getDate(), waiting.getTimeSlot(), waiting.getTheme());
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

    private void validateUser(final User user) {
        if (user == null) {
            throw new BusinessRuleViolationException("사용자 정보는 null일 수 없습니다.");
        }
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new BusinessRuleViolationException("예약 날짜는 null일 수 없습니다.");
        }
    }

    private void validateTimeSlot(final TimeSlot timeSlot) {
        if (timeSlot == null) {
            throw new BusinessRuleViolationException("시간 정보는 null일 수 없습니다.");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new BusinessRuleViolationException("테마 정보는 null일 수 없습니다.");
        }
    }
}

