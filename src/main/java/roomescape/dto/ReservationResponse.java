package roomescape.dto;

import roomescape.domain.Reservation;

import java.time.LocalDate;

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
                LoginMemberResponse.from(reservation.getMember())
        );
    }
}
