package roomescape.reservation.dto;

import java.time.LocalDate;

import roomescape.member.entity.Member;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.entity.Reservation;
import roomescape.theme.entity.Theme;
import roomescape.time.entity.ReservationTime;

public record ReservationRequest(LocalDate date, long timeId, long themeId) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme, ReservationStatus reservationStatus) {
        return new Reservation(this.date, reservationTime, theme, member, reservationStatus);
    }

    public static ReservationRequest from(ReservationPaymentRequest reservationPaymentRequest) {
        return new ReservationRequest(reservationPaymentRequest.date(), reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());
    }
}
