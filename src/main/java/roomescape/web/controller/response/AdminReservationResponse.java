package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.request.ReservationSaveDto;

public record AdminReservationResponse(LocalDate date, Long themeId, Long timeId, Long memberId) {

    public static AdminReservationResponse from(ReservationSaveDto reservationSaveDto) {
        return new AdminReservationResponse(
                LocalDate.parse(reservationSaveDto.date()),
                reservationSaveDto.themeId(),
                reservationSaveDto.timeId(),
                reservationSaveDto.memberId());
    }
}
