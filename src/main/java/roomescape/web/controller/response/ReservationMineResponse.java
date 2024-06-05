package roomescape.web.controller.response;

import roomescape.service.response.ReservationPaymentDto;

import java.time.LocalDate;

public record ReservationMineResponse(
        Long reservationId,
        LocalDate date,
        ThemeResponse theme,
        ReservationTimeResponse time,
        PaymentResponse payment) {

    public ReservationMineResponse(ReservationPaymentDto reservation) {
        this(reservation.reservationDto().id(),
                reservation.reservationDto().date().getDate(),
                new ThemeResponse(reservation.reservationDto().theme()),
                new ReservationTimeResponse(reservation.reservationDto().time()),
                new PaymentResponse(reservation.paymentDto()));
    }
}
