package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;

@Entity
public class ReservationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startAt;

    protected ReservationTime() {
    }

    public ReservationTime(LocalTime startAt) {
        this(null, startAt);
    }

    public ReservationTime(Long id, LocalTime startAt) {
        validateIsNull(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    private void validateIsNull(LocalTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("값을 입력하지 않았습니다.");
        }
    }

    public boolean sameTimeId(Long otherTimeId) {
        return id.equals(otherTimeId);
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
