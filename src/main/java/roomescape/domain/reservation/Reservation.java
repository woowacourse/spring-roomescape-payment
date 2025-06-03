package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.BusinessRuleViolationException;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reservation_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Reservation {

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

    protected Reservation(final LocalDate date, final TimeSlot timeSlot, final Theme theme, final User user) {
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
