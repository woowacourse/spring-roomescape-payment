package roomescape.service.request;

import roomescape.web.controller.request.MemberReservationRequest;

public record PaymentApproveDto(String paymentKey, String orderId, Long amount) {

    public static PaymentApproveDto from(MemberReservationRequest request) {
        return new PaymentApproveDto(request.paymentKey(), request.orderId(), request.amount());
    }
}
