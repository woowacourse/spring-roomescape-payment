package roomescape.service.httpclient;

import org.springframework.web.client.ResourceAccessException;
import roomescape.controller.request.PaymentRequest;
import roomescape.exception.PaymentException;

public abstract class TossPaymentClient {
    protected static final String CONFIRM_URI = "/confirm";
    public void confirm(PaymentRequest paymentRequest) {
        try {
            request(paymentRequest);
        } catch (ResourceAccessException e) {
            throw new PaymentException("에궁ㅜㅜ 타임아웃이에요");
        }
    }

    protected abstract void request(final PaymentRequest paymentRequest);
}