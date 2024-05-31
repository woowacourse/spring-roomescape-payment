package roomescape.domain.reservation.dto;

import roomescape.domain.member.dto.MemberDto;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationStatus;

public record ReservationDto(
        Long id,
        ReservationStatus status,
        ReservationDate date,
        ReservationTimeDto time,
        ThemeDto theme,
        MemberDto member
) {
    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getDate(),
                ReservationTimeDto.from(reservation.getTime()),
                ThemeDto.from(reservation.getTheme()),
                MemberDto.from(reservation.getMember())
        );
    }
}
