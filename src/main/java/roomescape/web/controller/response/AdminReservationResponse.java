package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.request.ReservationSaveAppRequest;

public record AdminReservationResponse(LocalDate date, Long themeId, Long timeId, Long memberId) {

    public static AdminReservationResponse from(ReservationSaveAppRequest reservationSaveAppRequest) {
        return new AdminReservationResponse(
                LocalDate.parse(reservationSaveAppRequest.date()),
                reservationSaveAppRequest.themeId(),
                reservationSaveAppRequest.timeId(),
                reservationSaveAppRequest.memberId());
    }
}
