package roomescape.exception.custom.reason.schedule;

import lombok.Getter;
import roomescape.exception.custom.status.ConflictException;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleConflictException extends ConflictException {

    private final LocalDate date;
    private final LocalTime startAt;
    private final String themeName;

    public ScheduleConflictException(final LocalDate date, final LocalTime startAt, final String themeName) {
        super("이미 존재하는 스케줄입니다.");
        this.date = date;
        this.startAt = startAt;
        this.themeName = themeName;
    }
}
