package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

public record ReservationDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto reservationTimeDto,
        ThemeDto themeDto
) {

    public static ReservationDto from(Reservation reservation) {
        Member member = reservation.getMember();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        return new ReservationDto(
                reservation.getId(),
                member.getName(),
                reservation.getDate(),
                ReservationTimeDto.from(time),
                ThemeDto.from(theme)
        );
    }
}
