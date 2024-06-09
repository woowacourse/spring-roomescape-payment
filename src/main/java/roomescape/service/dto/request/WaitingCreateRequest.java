package roomescape.service.dto.request;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationwaiting.ReservationWaiting;

import java.time.LocalDate;

public record WaitingCreateRequest(LocalDate date, Long timeId, Long themeId, Member member) {

    public ReservationWaiting toReservationWaiting(Reservation reservation) {
        return new ReservationWaiting(reservation, member);
    }
}
