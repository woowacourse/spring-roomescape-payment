package roomescape.exception.custom.reason.reservation;

import lombok.Getter;
import roomescape.exception.custom.status.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ReservationNotExistsScheduleException extends BadRequestException {

    private final LocalDate date;
    private final LocalTime startAt;

    public ReservationNotExistsScheduleException(final LocalDate date, final LocalTime startAt) {
        super("스케줄에 대한 예약이 존재하지 않습니다.");
        this.date = date;
        this.startAt = startAt;
    }
}
