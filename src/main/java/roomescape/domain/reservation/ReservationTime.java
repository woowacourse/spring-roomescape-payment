package roomescape.domain.reservation;

import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "reservation_time")
@Entity
public class ReservationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "start_at", unique = true)
    private LocalTime startAt;

    public ReservationTime(String startAt) {
        this(null, startAt);
    }

    public ReservationTime(Long id, String startAt) {
        this.id = id;
        this.startAt = LocalTime.parse(startAt);
    }

    protected ReservationTime() {
    }

    public boolean isBeforeNow() {
        return startAt.isBefore(LocalTime.now());
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return this.startAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationTime that = (ReservationTime) o;
        if (id == null || that.id == null) {
            return Objects.equals(startAt, that.startAt);
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(startAt);
        }
        return Objects.hash(id);
    }
}
