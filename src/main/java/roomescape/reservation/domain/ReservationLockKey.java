package roomescape.reservation.domain;

import java.time.LocalDate;

public record ReservationLockKey(LocalDate date, Long timeId, Long themeId) {

    private static final String FORMAT = "reservation_%s_%d_%d";

    public String generate() {
        return String.format(FORMAT, date, timeId, themeId);
    }
}
