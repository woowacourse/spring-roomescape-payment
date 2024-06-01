package roomescape.service.request;

import roomescape.web.controller.request.AdminReservationRequest;

public record ReservationSaveAppRequest(String date, Long timeId, Long themeId, Long memberId) {

    public static ReservationSaveAppRequest from(AdminReservationRequest request) {
        return new ReservationSaveAppRequest(
                request.date(),
                request.timeId(),
                request.themeId(),
                request.memberId()
        );
    }
}
