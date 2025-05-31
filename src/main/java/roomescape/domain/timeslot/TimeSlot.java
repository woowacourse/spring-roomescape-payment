package roomescape.domain.timeslot;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalTime startAt;

    private TimeSlot(final Long id, final LocalTime startAt) {
        validateStartAt(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    public static TimeSlot ofExisting(final long id, final LocalTime startAt) {
        return new TimeSlot(id, startAt);
    }

    public static TimeSlot register(final LocalTime startAt) {
        return new TimeSlot(null, startAt);
    }

    public boolean isTimeBefore(final LocalTime time) {
        return this.startAt.isBefore(time);
    }

    private void validateStartAt(LocalTime startAt) {
        if (startAt == null) {
            throw new BusinessRuleViolationException("시간은 null일 수 없습니다.");
        }
    }
}
