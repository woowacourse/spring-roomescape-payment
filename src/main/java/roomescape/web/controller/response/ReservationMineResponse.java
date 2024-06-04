package roomescape.web.controller.response;

import roomescape.service.response.ReservationPaymentDto;

import java.time.LocalDate;

public record ReservationMineResponse(
        Long reservationId,
        ThemeResponse theme,
        LocalDate date,
        ReservationTimeResponse time,
        PaymentResponse payment) {

    public ReservationMineResponse(ReservationPaymentDto reservation) {
        this(reservation.reservationDto().id(),
                new ThemeResponse(reservation.reservationDto().theme()),
                reservation.reservationDto().date().getDate(),
                new ReservationTimeResponse(reservation.reservationDto().time()),
                new PaymentResponse(reservation.paymentDto()));
    }
}
