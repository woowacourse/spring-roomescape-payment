package roomescape.payment.presentation.dto.response;

public record PaymentApproveResponse(String orderId, Long totalAmount) {

    public static PaymentApproveResponse from(final TossPaymentApproveResponse response) {
        return new PaymentApproveResponse(response.orderId(), response.totalAmount());
    }
}
