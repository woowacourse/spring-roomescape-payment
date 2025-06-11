package roomescape.exception.custom.reason.reservation;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ReservationPastDateException extends RuntimeException {

    private final LocalDate date;
    private final LocalTime startAt;

    public ReservationPastDateException(final LocalDate date, final LocalTime startAt) {
        super("과거 날짜로 예약할 수 없습니다.");
        this.date = date;
        this.startAt = startAt;
    }
}
