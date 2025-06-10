package roomescape.reservationTime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startAt;

    private ReservationTime(final Long id, final LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTime createWithoutId(final LocalTime startAt) {
        return new ReservationTime(null, startAt);
    }

    public static ReservationTime createWithId(final Long id, final LocalTime startAt) {
        return new ReservationTime(Objects.requireNonNull(id), startAt);
    }

    public boolean isSameTime(final ReservationTime time) {
        return this.startAt.equals(time.startAt);
    }

    public boolean isBeforeTime(final LocalTime time) {
        return startAt.isBefore(time);
    }
}
