package roomescape.web.controller.response;

import roomescape.service.response.ReservationDto;

import java.time.LocalDate;

public record ReservationMineResponse(
        Long reservationId,
        LocalDate date,
        ThemeResponse theme,
        ReservationTimeResponse time,
        PaymentResponse payment) {

    public ReservationMineResponse(ReservationDto reservationDto) {
        this(reservationDto.id(),
                reservationDto.date().getDate(),
                new ThemeResponse(reservationDto.theme()),
                new ReservationTimeResponse(reservationDto.time()),
                new PaymentResponse(reservationDto.paymentDto()));
    }
}
