package roomescape.service.response;

import java.util.List;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

public record ReservationAppResponse(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeAppResponse reservationTimeAppResponse,
        ThemeAppResponse themeAppResponse,
        List<PaymentAppResponse> paymentAppResponses
) {

    public static ReservationAppResponse withoutPayments(Reservation reservation) {
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

    public static ReservationAppResponse withPayments(Reservation reservation) {
        Member member = reservation.getMember();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        List<Payment> payments = reservation.getPayments();
        return new ReservationAppResponse(
                reservation.getId(),
                member.getName(),
                reservation.getDate(),
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme),
                PaymentAppResponse.from(payments)
        );
    }
}
