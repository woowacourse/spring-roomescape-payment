package roomescape.payment.presentation.dto.request;

public record TossPaymentApproveRequest(String paymentKey, String orderId, Long amount, Long reservationId) {

    public static TossPaymentApproveRequest from(final PaymentRequest paymentRequest) {
        return new TossPaymentApproveRequest(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount(), paymentRequest.reservationId());
    }
}
