package roomescape.payment.presentation.dto.request;

public record PaymentApproveRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentApproveRequest from(final PaymentRequest paymentRequest) {
        return new PaymentApproveRequest(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount());
    }
}
