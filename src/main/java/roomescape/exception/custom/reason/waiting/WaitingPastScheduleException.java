package roomescape.exception.custom.reason.waiting;

import lombok.Getter;
import roomescape.exception.custom.status.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class WaitingPastScheduleException extends BadRequestException {

    private final LocalDate date;
    private final LocalTime startAt;

    public WaitingPastScheduleException(final LocalDate date, final LocalTime startAt) {
        super("과거에 대한 예약을 할 수 없습니다");
        this.date = date;
        this.startAt = startAt;
    }
}
