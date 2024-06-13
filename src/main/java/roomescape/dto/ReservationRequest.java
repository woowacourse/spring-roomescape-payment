package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

public record ReservationRequest(LocalDate date, long timeId, long themeId) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme, Payment payment) {
        return new Reservation(null, this.date, reservationTime, theme, member, LocalDateTime.now(),
                ReservationStatus.WAITING, payment);
    }

    public static ReservationRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new ReservationRequest(reservationWithPaymentRequest.date(),
                reservationWithPaymentRequest.timeId(),
                reservationWithPaymentRequest.themeId());
    }
}
