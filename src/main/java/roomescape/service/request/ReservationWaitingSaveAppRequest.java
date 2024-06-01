package roomescape.service.request;

import roomescape.web.controller.request.ReservationWaitingRequest;

public record ReservationWaitingSaveAppRequest(String date, Long timeId, Long themeId, Long memberId) {

    public static ReservationWaitingSaveAppRequest of(ReservationWaitingRequest request, Long memberId) {
        return new ReservationWaitingSaveAppRequest(request.date(), request.timeId(), request.themeId(), memberId);
    }
}
