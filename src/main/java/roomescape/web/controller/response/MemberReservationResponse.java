package roomescape.web.controller.response;

import java.time.LocalDate;

import roomescape.service.response.ReservationPaymentDto;
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
                new ReservationTimeResponse(appResponse.time()),
                new ThemeResponse(appResponse.theme()),
                null
        );
    }

    public MemberReservationResponse(ReservationPaymentDto paidReservation) {
        this(
                paidReservation.reservationDto().id(),
                paidReservation.reservationDto().name(),
                paidReservation.reservationDto().date().getDate(),
                new ReservationTimeResponse(paidReservation.reservationDto().time()),
                new ThemeResponse(paidReservation.reservationDto().theme()),
                new PaymentResponse(paidReservation.paymentDto())
        );
    }
}
