package roomescape.service.dto.request;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;

public record AdminReservationCreateRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        Long memberId
) {

    public Reservation toReservation(Member member, ReservationTime time, Theme theme) {
        return new Reservation(date, member, time, theme);
    }
}
