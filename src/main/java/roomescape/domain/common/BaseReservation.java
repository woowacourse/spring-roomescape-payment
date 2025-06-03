package roomescape.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.BusinessRuleViolationException;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class BaseReservation {

    @Column(nullable = false)
    protected LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected Theme theme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected User user;

    protected BaseReservation(final LocalDate date, final TimeSlot timeSlot, final Theme theme, final User user) {
        validateDate(date);
        validateTimeSlot(timeSlot);
        validateTheme(theme);
        validateUser(user);

        this.date = date;
        this.timeSlot = timeSlot;
        this.theme = theme;
        this.user = user;
    }

    protected void validateDate(final LocalDate date) {
        if (date == null) {
            throw new BusinessRuleViolationException("예약 날짜는 null일 수 없습니다.");
        }
    }

    protected void validateTimeSlot(final TimeSlot timeSlot) {
        if (timeSlot == null) {
            throw new BusinessRuleViolationException("시간 정보는 null일 수 없습니다.");
        }
    }

    protected void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new BusinessRuleViolationException("테마 정보는 null일 수 없습니다.");
        }
    }

    protected void validateUser(final User user) {
        if (user == null) {
            throw new BusinessRuleViolationException("사용자 정보는 null일 수 없습니다.");
        }
    }
}
