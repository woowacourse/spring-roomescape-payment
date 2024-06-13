package roomescape.core.dto.reservation;

import roomescape.core.domain.Reservation;
import roomescape.core.dto.member.MemberResponse;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.dto.theme.ThemeResponse;

public record ReservationResponse(Long id, String date, MemberResponse member, ReservationTimeResponse time,
                                  ThemeResponse theme) {

    public static ReservationResponse from(final Reservation reservation) {
        final Long id = reservation.getId();
        final String date = reservation.getDateString();
        final MemberResponse member = MemberResponse.from(reservation.getMember());
        final ReservationTimeResponse time = ReservationTimeResponse.from(reservation.getReservationTime());
        final ThemeResponse theme = ThemeResponse.from(reservation.getTheme());

        return new ReservationResponse(id, date, member, time, theme);
    }
}
