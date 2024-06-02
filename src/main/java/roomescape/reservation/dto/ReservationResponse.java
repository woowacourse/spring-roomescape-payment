package roomescape.reservation.dto;

import java.time.LocalDate;

import roomescape.auth.dto.LoginMemberResponse;
import roomescape.reservation.entity.Reservation;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;

public record ReservationResponse(long id,
                                  LocalDate date,
                                  ReservationTimeResponse time,
                                  ThemeResponse theme,
                                  LoginMemberResponse member) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getLoginMember())
        );
    }
}
