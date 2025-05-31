package roomescape.domain.waiting;

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
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class Waiting {

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

    private Waiting(final Long id,
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

    public static Waiting register(final User user,
                                   final LocalDate date,
                                   final TimeSlot timeSlot,
                                   final Theme theme) {

        Waiting waiting = new Waiting(null, user, date, timeSlot, theme);
        validateNotPastDateTime(date, timeSlot);
        return waiting;
    }

    private static void validateNotPastDateTime(final LocalDate date, final TimeSlot timeSlot) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        boolean isPastDate = date.isBefore(currentDate);
        boolean isCurrentDateAndPastTime = date.isEqual(currentDate) && timeSlot.isTimeBefore(currentTime);

        if (isPastDate || isCurrentDateAndPastTime) {
            throw new BusinessRuleViolationException("이전 날짜로 예약 대기 신청할 수 없습니다.");
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
