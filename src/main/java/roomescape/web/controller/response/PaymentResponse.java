package roomescape.web.controller.response;

import java.math.BigDecimal;
import java.util.List;
import roomescape.service.response.PaymentAppResponse;

public record PaymentResponse(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentResponse from(PaymentAppResponse paymentAppResponse) {
        return new PaymentResponse(
                paymentAppResponse.paymentKey(),
                paymentAppResponse.orderId(),
                paymentAppResponse.amount()
        );
    }

    public static List<PaymentResponse> from(List<PaymentAppResponse> paymentAppResponses) {
        return paymentAppResponses.stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
