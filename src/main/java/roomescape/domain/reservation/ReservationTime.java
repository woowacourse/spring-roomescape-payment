package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.infrastructure.error.exception.ReservationTimeException;

import java.time.LocalTime;
import java.util.Objects;

@Entity
public class ReservationTime {

    private static final LocalTime RESERVATION_START_TIME = LocalTime.of(12, 0);
    private static final LocalTime RESERVATION_END_TIME = LocalTime.of(22, 0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startAt;

    public ReservationTime(final LocalTime startAt) {
        this(null, startAt);
    }

    public ReservationTime(final Long id, final LocalTime startAt) {
        if (startAt.isBefore(RESERVATION_START_TIME) || startAt.isAfter(RESERVATION_END_TIME)) {
            throw new ReservationTimeException("해당 시간은 예약 가능 시간이 아닙니다.");
        }
        this.id = id;
        this.startAt = startAt;
    }

    protected ReservationTime() {
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final var that = (ReservationTime) obj;
        return Objects.equals(this.id, that.id) &&
               Objects.equals(this.startAt, that.startAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startAt);
    }
}
