package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public record AdminReservationSaveRequest(LocalDate date, Long themeId, Long timeId, Long memberId) {

    public Reservation toEntity(Member member, Theme theme, ReservationTime reservationTime, ReservationStatus reservationStatus) {
        return new Reservation(member, date, theme, reservationTime, reservationStatus);
    }
}
