package roomescape.business.dto;

import java.time.LocalDate;
import roomescape.business.model.vo.ReservationStatus;

public record ReservationSpecDto(LocalDate date, String timeIdValue, String themeIdValue,
                                 String userIdValue, ReservationStatus reservationStatus) {

    public static ReservationSpecDto of(LocalDate date, String timeIdValue, String themeIdValue,
                                        String userIdValue, ReservationStatus reservationStatus) {
        return new ReservationSpecDto(date, timeIdValue, themeIdValue, userIdValue, reservationStatus);
    }
}
