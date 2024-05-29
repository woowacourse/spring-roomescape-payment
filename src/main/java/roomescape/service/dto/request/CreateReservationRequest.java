package roomescape.service.dto.request;

import java.time.LocalDate;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.theme.Theme;

public record CreateReservationRequest(LocalDate date, long timeId, long themeId, long memberId, String paymentKey, String orderId, int amount) {

    public Reservation toReservation(ReservationTime time, Theme theme, Member member) {
        return new Reservation(date, member, time, theme);
    }

    public ReservationWaiting toReservationWaiting(Reservation reservation, Member member) {
        return new ReservationWaiting(reservation, member);
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount);
    }
}
