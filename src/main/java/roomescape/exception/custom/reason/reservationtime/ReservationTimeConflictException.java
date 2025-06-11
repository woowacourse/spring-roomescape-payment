package roomescape.exception.custom.reason.reservationtime;

import lombok.Getter;
import roomescape.exception.custom.status.ConflictException;

import java.time.LocalTime;

@Getter
public class ReservationTimeConflictException extends ConflictException {

    private final LocalTime startAt;

    public ReservationTimeConflictException(final LocalTime startAt) {
        super("예약시간이 이미 존재합니다.");
        this.startAt = startAt;
    }
}
