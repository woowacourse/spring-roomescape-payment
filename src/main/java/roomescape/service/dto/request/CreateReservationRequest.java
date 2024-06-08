package roomescape.service.dto.request;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;

public record CreateReservationRequest(LocalDate date, long timeId, long themeId, long memberId) {

    public Reservation toReservation(ReservationTime time, Theme theme, Member member) {
        return new Reservation(date, member, time, theme);
    }

    public ReservationWaiting toReservationWaiting(Reservation reservation, Member member) {
        return new ReservationWaiting(reservation, member);
    }
}