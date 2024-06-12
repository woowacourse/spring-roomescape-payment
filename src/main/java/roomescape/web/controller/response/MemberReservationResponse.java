package roomescape.web.controller.response;

import java.time.LocalDate;

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
                new PaymentResponse(appResponse.paymentDto())
        );
    }
}
