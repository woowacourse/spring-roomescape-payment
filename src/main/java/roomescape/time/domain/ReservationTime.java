package roomescape.time.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Entity
@Table(name = "reservation_time")
public class ReservationTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "start_at", nullable = false, unique = true)
    private LocalTime startAt;

    public ReservationTime(LocalTime startAt) {
        this.startAt = Optional.ofNullable(startAt).orElseThrow(
                () -> new RoomEscapeException("예약 시간은 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    public ReservationTime(Long id, LocalTime startAt) {
        this(startAt);
        this.id = Optional.ofNullable(id).orElseThrow(
                () -> new RoomEscapeException("예약 시간 id는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        ;
    }

    protected ReservationTime() {
    }

    public ReservationTime withId(Long id) {
        return new ReservationTime(id, this.startAt);
    }

    public boolean isBefore(LocalTime currentTime) {
        return startAt.isBefore(currentTime);
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservationTime that = (ReservationTime) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReservationTime{" +
               "id=" + id +
               ", startAt=" + startAt +
               '}';
    }
}
