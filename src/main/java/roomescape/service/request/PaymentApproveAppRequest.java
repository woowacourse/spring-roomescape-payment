package roomescape.service.request;

import roomescape.web.controller.request.MemberReservationRequest;

public record PaymentApproveAppRequest(String paymentKey, String orderId, Long totalAmount) {

    public static PaymentApproveAppRequest from(MemberReservationRequest request) {
        return new PaymentApproveAppRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
