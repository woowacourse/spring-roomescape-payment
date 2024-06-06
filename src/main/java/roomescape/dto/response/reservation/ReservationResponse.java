package roomescape.dto.response.reservation;

import java.time.LocalDate;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.response.member.MemberResponse;
import roomescape.dto.response.theme.ThemeResponse;

public record ReservationResponse(
        long id, MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme())
        );
    }
}
