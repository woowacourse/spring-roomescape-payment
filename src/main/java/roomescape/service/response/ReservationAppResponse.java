package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWithPayment;
import roomescape.domain.Theme;

public record ReservationAppResponse(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeAppResponse reservationTimeAppResponse,
        ThemeAppResponse themeAppResponse,
        PaymentAppResponse paymentAppResponse
) {

    public static ReservationAppResponse from(Reservation reservation) {
        Member member = reservation.getMember();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        return new ReservationAppResponse(
                reservation.getId(),
                member.getName(),
                reservation.getDate(),
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme),
                null
        );
    }

    public static ReservationAppResponse of(Reservation reservation, Payment payment) {
        Member member = reservation.getMember();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        return new ReservationAppResponse(
                reservation.getId(),
                member.getName(),
                reservation.getDate(),
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme),
                PaymentAppResponse.from(payment)
        );
    }

    public static ReservationAppResponse from(ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.reservation();
        Payment payment = reservationWithPayment.payment();
        if (payment == null) {
            return from(reservation);
        }
        return of(reservation, payment);
    }
}
