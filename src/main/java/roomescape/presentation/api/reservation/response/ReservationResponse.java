package roomescape.presentation.api.reservation.response;

import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.presentation.api.member.MemberResponse;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {

    public static ReservationResponse from(final ReservationResult reservationResult) {
        return new ReservationResponse(
                reservationResult.id(),
                MemberResponse.from(reservationResult.memberResult()),
                reservationResult.date(),
                ReservationTimeResponse.from(reservationResult.time()),
                ThemeResponse.from(reservationResult.theme())
        );
    }
}
