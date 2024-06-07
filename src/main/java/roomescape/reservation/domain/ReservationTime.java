package roomescape.reservation.domain;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation_time")
public class ReservationTime {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    protected ReservationTime() {
    }

    public ReservationTime(LocalTime startAt) {
        this(null, startAt);
        validateIsNull(startAt);
    }

    public ReservationTime(Long id, LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    private void validateIsNull(LocalTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("값을 입력하지 않았습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
