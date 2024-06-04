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
            throw new PaymentException("요청 시간이 만료되었습니다. 다시 요청해주세요.");
        }
    }

    protected abstract void request(final PaymentRequest paymentRequest);
}
