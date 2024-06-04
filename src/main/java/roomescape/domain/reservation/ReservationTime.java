package roomescape.domain.reservation;

import static roomescape.exception.RoomescapeExceptionCode.EMPTY_TIME;
import static roomescape.exception.RoomescapeExceptionCode.INVALID_TIME_FORMAT;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import roomescape.exception.RoomescapeException;

@Entity
public class ReservationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startAt;

    protected ReservationTime() {
    }

    public ReservationTime(final String startAt) {
        this(null, startAt);
    }

    public ReservationTime(final Long id, final String startAt) {
        this(id, convertToLocalTime(startAt));
    }

    public ReservationTime(final Long id, final LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    private static LocalTime convertToLocalTime(final String time) {
        if (time == null || time.isEmpty()) {
            throw new RoomescapeException(EMPTY_TIME);
        }
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new RoomescapeException(INVALID_TIME_FORMAT);
        }
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ReservationTime other
                && Objects.equals(getId(), other.getId())
                && Objects.equals(getStartAt(), other.getStartAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startAt);
    }
}
