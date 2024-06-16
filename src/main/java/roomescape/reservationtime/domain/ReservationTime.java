package roomescape.reservationtime.domain;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.ReservationTimeExceptionCode;

@Tag(name = "예약 시간 엔티티", description = "예약 시간 관련 정보 관리 & 예외처리")
@Entity
public class ReservationTime {

    private static final int NULL_ID = 0;
    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    public ReservationTime() {
    }

    public ReservationTime(long id, LocalTime startAt) {
        validateTime(startAt);

        this.id = id;
        this.startAt = startAt;
    }

    public ReservationTime(LocalTime startAt) {
        this(NULL_ID, startAt);
    }

    public long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public void validateTime(LocalTime startAt) {
        if (startAt == null) {
            throw new RoomEscapeException(ReservationTimeExceptionCode.FOUND_TIME_IS_NULL_EXCEPTION);
        }
        if (startAt.isBefore(OPEN_TIME) || startAt.isAfter(CLOSE_TIME)) {
            throw new RoomEscapeException(ReservationTimeExceptionCode.TIME_IS_OUT_OF_OPERATING_TIME);
        }
    }

    public boolean isBeforeTime(LocalTime time) {
        return startAt.isBefore(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationTime time = (ReservationTime) o;
        return id == time.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
