package roomescape.dto;

import java.time.LocalDate;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

public record ReservationRequest(LocalDate date, long timeId, long themeId) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme) {
        return new Reservation(this.date, reservationTime, theme, member);
    }

    public static ReservationRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new ReservationRequest(reservationWithPaymentRequest.date(),
                reservationWithPaymentRequest.timeId(),
                reservationWithPaymentRequest.themeId());
    }
}
