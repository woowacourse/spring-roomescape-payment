package roomescape.service.reservation.dto;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;

public record ReservationSaveInput(LocalDate date, Long timeId, Long themeId) {
    public Reservation toReservation(ReservationTime time, Theme theme, Member member) {
        return new Reservation(date, time, theme, member, ReservationStatus.BOOKED);
    }
}
