package roomescape.global.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class DateTimeParser {

    public static LocalDateTime parse(final String value) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(value);
        return offsetDateTime.toLocalDateTime();
    }
}
