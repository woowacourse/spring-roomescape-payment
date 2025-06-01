package roomescape.reservation.time.domain;

import java.time.LocalTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class ReservationTime {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id", nullable = false))
    private ReservationTimeId id;

    @Column(nullable = false, unique = true)
    private LocalTime startAt;

    protected ReservationTime() {}

    public ReservationTime(final Long id, final LocalTime startAt) {
        validateNull(startAt);
        this.id = new ReservationTimeId(id);
        this.startAt = startAt;
    }

    public ReservationTime(final LocalTime startAt) {
        this(null, startAt);
    }

    private void validateNull(final LocalTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("시작 시간이 존재하지 않습니다.");
        }
    }

    public Long getId() {
        return id.getValue();
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
