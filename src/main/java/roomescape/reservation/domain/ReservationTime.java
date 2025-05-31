package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.common.exception.BadRequestException;

import java.time.LocalTime;

@Entity
public class ReservationTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalTime startAt;

    protected ReservationTime() {}

    public ReservationTime(final Long id, final LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
        validateReservationTime();
    }

    public ReservationTime(final LocalTime startAt) {
        this(null, startAt);
    }

    public void validateReservationTime() {
        if (startAt == null) {
            throw new BadRequestException("시간이 입력되지 않았습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
