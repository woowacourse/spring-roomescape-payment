package roomescape.service.request;

import roomescape.web.controller.request.AdminReservationRequest;

public record ReservationSaveDto(String date, Long timeId, Long themeId, Long memberId) {

    public static ReservationSaveDto from(AdminReservationRequest request) {
        return new ReservationSaveDto(
                request.date(),
                request.timeId(),
                request.themeId(),
                request.memberId()
        );
    }
}
