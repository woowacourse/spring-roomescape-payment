package roomescape.dto.response.reservation;

import java.time.LocalDate;
import roomescape.domain.reservation.Reservation;

public record ReservationInformResponse(
        long id, long memberId,
        LocalDate date,
        long timeId,
        long themeId
) {
    public static ReservationInformResponse from(Reservation reservation) {
        return new ReservationInformResponse(
                reservation.getId(), reservation.getMember().getId(), reservation.getDate(),
                reservation.getTime().getId(), reservation.getTheme().getId());
    }
}
