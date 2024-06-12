package roomescape.domain.reservation.dto;

import roomescape.domain.member.dto.MemberResponse;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String status
) {
    public static ReservationResponse from(final ReservationDto reservation) {
        return new ReservationResponse(
                reservation.id(),
                new MemberResponse(
                        reservation.member().id(),
                        reservation.member().name().getValue(),
                        reservation.member().email().getValue()
                ),
                reservation.date().getValue(),
                ReservationTimeResponse.from(reservation.time()),
                ThemeResponse.from(reservation.theme()),
                reservation.status().getDescription()
        );
    }
}
