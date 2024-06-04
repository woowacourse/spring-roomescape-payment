package roomescape.dto;

import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import java.time.LocalDate;

public record ReservationRequest(LocalDate date, long timeId, long themeId) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme) {
        return new Reservation(this.date, reservationTime, theme, member, null, 0L);//TODO
    }

    public static ReservationRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new ReservationRequest(reservationWithPaymentRequest.date(),
                reservationWithPaymentRequest.timeId(),
                reservationWithPaymentRequest.themeId());
    }
}
