package roomescape.web.controller.response;

import java.math.BigDecimal;
import roomescape.service.response.PaymentAppResponse;

public record PaymentResponse(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentResponse from(PaymentAppResponse paymentAppResponse) {
        if (paymentAppResponse == null) {
            return null;
        }
        return new PaymentResponse(
                paymentAppResponse.paymentKey(),
                paymentAppResponse.orderId(),
                paymentAppResponse.amount()
        );
    }
}
