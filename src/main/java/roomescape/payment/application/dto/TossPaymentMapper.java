package roomescape.payment.application.dto;

import org.springframework.stereotype.Component;
import roomescape.payment.presentation.dto.request.TossPaymentApproveRequest;
import roomescape.payment.presentation.dto.response.TossPaymentApproveResponse;

@Component
public class TossPaymentMapper {

    public TossPaymentApproveRequest toTossRequest(final PaymentGatewayRequest request) {
        return new TossPaymentApproveRequest(request.paymentKey(), request.orderId(),
                request.amount(), request.reservationId());
    }

    public PaymentGatewayResponse toGatewayResponse(final TossPaymentApproveResponse response) {
        return new PaymentGatewayResponse(response.paymentKey(), response.orderId(), response.totalAmount());
    }
}
