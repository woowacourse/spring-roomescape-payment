package roomescape.service.request;

import roomescape.web.controller.request.AdminReservationRequest;
import roomescape.web.controller.request.MemberReservationRequest;

public record ReservationSaveAppRequest(
        String date,
        Long timeId,
        Long themeId,
        Long memberId,
        PaymentApproveAppRequest paymentApproveAppRequest
) {

    public static ReservationSaveAppRequest from(AdminReservationRequest adminReservationRequest) {
        return new ReservationSaveAppRequest(
                adminReservationRequest.date(),
                adminReservationRequest.timeId(),
                adminReservationRequest.themeId(),
                adminReservationRequest.memberId(),
                null
        );
    }

    public static ReservationSaveAppRequest of(MemberReservationRequest memberReservationRequest, Long memberId) {
        return new ReservationSaveAppRequest(
                memberReservationRequest.date(),
                memberReservationRequest.timeId(),
                memberReservationRequest.themeId(),
                memberId,
                PaymentApproveAppRequest.from(memberReservationRequest)
        );
    }
}
