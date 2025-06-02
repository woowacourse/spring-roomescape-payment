package roomescape.admin.dto;

import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;

public record AdminReservationResponse(
        Long id,
        MemberResponse member,
        ThemeResponse theme,
        ReservationTimeResponse time,
        LocalDate date
) {
    public static AdminReservationResponse from(final Reservation reservation) {
        return new AdminReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()),
                ReservationTimeResponse.from(reservation.getTime()),
                reservation.getDate()
        );
    }
}
