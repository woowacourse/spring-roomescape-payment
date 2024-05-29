package roomescape.service.request;

import roomescape.web.controller.request.MemberReservationRequest;

public record PaymentApproveDto(String paymentKey, String orderId, Long amount) {

    public PaymentApproveDto(MemberReservationRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }
}
