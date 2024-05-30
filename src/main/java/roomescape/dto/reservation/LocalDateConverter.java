package roomescape.dto.reservation;

import static roomescape.exception.RoomescapeExceptionCode.EMPTY_DATE;
import static roomescape.exception.RoomescapeExceptionCode.INVALID_DATE_FORMAT;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import roomescape.exception.RoomescapeException;

public class LocalDateConverter {

    private LocalDateConverter() {
    }

    public static LocalDate toLocalDate(final String date) {
        if (date == null || date.isEmpty()) {
            throw new RoomescapeException(EMPTY_DATE);
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new RoomescapeException(INVALID_DATE_FORMAT);
        }
    }
}
