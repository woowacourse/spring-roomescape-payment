package roomescape.service.httpclient;

import org.springframework.web.client.ResourceAccessException;
import roomescape.exception.PaymentException;
import roomescape.request.PaymentRequest;
import roomescape.response.PaymentResponse;

public abstract class TossPaymentClient {
    protected static final String CONFIRM_URI = "/confirm";

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return request(paymentRequest);
        } catch (ResourceAccessException e) {
            throw new PaymentException("요청 시간이 만료되었습니다. 다시 요청해주세요.");
        }
    }

    protected abstract PaymentResponse request(final PaymentRequest paymentRequest);
}
