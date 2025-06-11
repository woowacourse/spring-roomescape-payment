package roomescape.integration.helper;

import java.time.LocalDate;

public class DateUtils {

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate getTomorrow() {
        return LocalDate.now().plusDays(1);
    }
}
