package roomescape.business.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import roomescape.business.model.vo.Id;
import roomescape.exception.reservation.TimeSlotStartTimeRangeException;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
@Entity
public class TimeSlot {

    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(23, 0);
    private static final int MINUTE_INTERVAL = 30;

    @EmbeddedId
    private final Id id;
    private LocalTime startAt;

    protected TimeSlot() {
        id = Id.issue();
    }

    public static TimeSlot create(final LocalTime startAt) {
        validateAvailableTime(startAt);
        return new TimeSlot(Id.issue(), startAt);
    }

    public static TimeSlot restore(final String id, final LocalTime startAt) {
        return new TimeSlot(Id.create(id), startAt);
    }

    private static void validateAvailableTime(final LocalTime time) {
        if (time.isBefore(START_TIME) || time.isAfter(END_TIME)) {
            throw new TimeSlotStartTimeRangeException(START_TIME, END_TIME);
        }
    }

    public LocalTime startInterval() {
        return startAt.minusMinutes(MINUTE_INTERVAL);
    }

    public LocalTime endInterval() {
        return startAt.plusMinutes(MINUTE_INTERVAL);
    }
}
