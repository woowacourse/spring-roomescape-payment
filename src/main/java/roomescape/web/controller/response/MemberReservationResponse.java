package roomescape.web.controller.response;

import java.time.LocalDate;

import roomescape.service.response.PaidReservationDto;
import roomescape.service.response.ReservationDto;

public record MemberReservationResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        PaymentResponse payment) {

    public MemberReservationResponse(ReservationDto appResponse) {
        this(
                appResponse.id(),
                appResponse.name(),
                appResponse.date().getDate(),
                new ReservationTimeResponse(appResponse.reservationTimeDto()),
                new ThemeResponse(appResponse.themeDto()),
                null
        );
    }

    public MemberReservationResponse(PaidReservationDto paidReservation) {
        this(
                paidReservation.id(),
                paidReservation.name(),
                paidReservation.date(),
                new ReservationTimeResponse(paidReservation.time()),
                new ThemeResponse(paidReservation.themeDto()),
                new PaymentResponse(paidReservation.paymentDto())
        );
    }
}
