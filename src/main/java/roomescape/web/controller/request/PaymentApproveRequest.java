package roomescape.web.controller.request;

public record PaymentApproveRequest(String paymentKey, String orderId, Long amount) {

    public PaymentApproveRequest(MemberReservationRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }
}
