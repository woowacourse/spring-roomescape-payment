package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;

public record CreateReservationResponse(
        Long id,
        MemberResponse member,
        ThemeResponse theme,
        ReservationTimeResponse time,
        LocalDate date
) {

    public static CreateReservationResponse from(final Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()),
                ReservationTimeResponse.from(reservation.getTime()),
                reservation.getDate()
        );
    }
}
