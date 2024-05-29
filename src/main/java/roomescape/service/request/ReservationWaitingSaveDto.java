package roomescape.service.request;

import roomescape.web.controller.request.ReservationWaitingRequest;

public record ReservationWaitingSaveDto(String date, Long timeId, Long themeId, Long memberId) {

    public static ReservationWaitingSaveDto of(ReservationWaitingRequest request, Long memberId) {
        return new ReservationWaitingSaveDto(request.date(), request.timeId(), request.themeId(), memberId);
    }
}
