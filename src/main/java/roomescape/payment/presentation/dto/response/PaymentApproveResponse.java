package roomescape.payment.presentation.dto.response;

import roomescape.payment.application.dto.PaymentGatewayResponse;

public record PaymentApproveResponse(String orderId, Long totalAmount) {

    public static PaymentApproveResponse from(final PaymentGatewayResponse response) {
        return new PaymentApproveResponse(response.orderId(), response.amount());
    }
}
