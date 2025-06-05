package roomescape.domain.time.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.time.exception.ReservationTimeException;

@Getter
@NoArgsConstructor
@Entity
public class ReservationTime {

    private static final LocalTime OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startAt;

    public ReservationTime(final Long id, final LocalTime startAt) {
        validateStartAt(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    public ReservationTime(final LocalTime startAt) {
        this(null, startAt);
    }

    private void validateStartAt(final LocalTime startAt) {
        validateNull(startAt);
        validateTimeRange(startAt);
    }

    private void validateNull(LocalTime startAt) {
        if (startAt == null) {
            throw new ReservationTimeException("예약 시간은 NULL이 허용되지 않습니다.");
        }
    }

    private void validateTimeRange(final LocalTime startAt) {
        if (startAt.isBefore(OPEN_TIME) || startAt.isAfter(CLOSE_TIME)) {
            throw new ReservationTimeException("예약 시간은 " + OPEN_TIME + "부터 " + CLOSE_TIME + " 사이여야 합니다.");
        }
    }
}
