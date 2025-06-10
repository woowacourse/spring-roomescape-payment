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
import lombok.experimental.Accessors;
import roomescape.common.exception.BusinessException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@EqualsAndHashCode(of = "id")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startAt;

    private TimeSlot(final Long id, final LocalTime startAt) {
        validateIsNonNull(startAt);

        this.id = id;
        this.startAt = startAt;
    }

    public static TimeSlot createWithoutId(final LocalTime startAt) {
        return new TimeSlot(null, startAt);
    }
    
    private void validateIsNonNull(final Object object) {
        if (object == null) {
            throw new BusinessException("시간 정보는 필수입니다.");
        }
    }

    public boolean isBefore(final LocalTime time) {
        return this.startAt.isBefore(time);
    }

    public boolean isSame(final LocalTime time) {
        return startAt.equals(time);
    }
}
