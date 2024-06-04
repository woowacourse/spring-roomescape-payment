package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public record UserReservationSaveRequest(LocalDate date, Long themeId, Long timeId, String paymentKey, String orderId, int amount) {

    public Reservation toEntity(Member member, Theme theme, ReservationTime reservationTime, ReservationStatus reservationStatus) {
        return new Reservation(member, date, theme, reservationTime, reservationStatus);
    }
}
