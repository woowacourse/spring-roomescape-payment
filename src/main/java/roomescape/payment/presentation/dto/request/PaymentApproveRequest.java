package roomescape.payment.presentation.dto.request;

public record PaymentApproveRequest(String paymentKey, String orderId, Long amount, Long reservationId) {

    public static PaymentApproveRequest from(final PaymentRequest paymentRequest) {
        return new PaymentApproveRequest(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount(), paymentRequest.reservationId());
    }
}
