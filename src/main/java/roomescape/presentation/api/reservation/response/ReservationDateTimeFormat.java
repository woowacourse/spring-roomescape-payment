package roomescape.presentation.api.reservation.response;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public enum ReservationDateTimeFormat {

    TIME("HH:mm"),
    ;

    private final DateTimeFormatter formatter;

    ReservationDateTimeFormat(final String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    public String format(final TemporalAccessor temporal) {
        return formatter.format(temporal);
    }
}
