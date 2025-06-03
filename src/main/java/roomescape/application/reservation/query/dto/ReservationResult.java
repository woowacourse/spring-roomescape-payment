package roomescape.application.reservation.query.dto;

import roomescape.application.member.query.dto.MemberResult;
import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;

public record ReservationResult(
        Long id,
        MemberResult memberResult,
        LocalDate date,
        ReservationTimeResult time,
        ThemeResult theme
) {

    public static ReservationResult from(final Reservation reservation) {
        return new ReservationResult(
                reservation.getId(),
                MemberResult.from(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResult.from(reservation.getTime()),
                ThemeResult.from(reservation.getTheme()));
    }
}
