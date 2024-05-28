package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.payment.PaymentErrorResponse;
import roomescape.payment.PaymentRequest;
import roomescape.payment.PaymentResponse;
import roomescape.payment.exception.PaymentException;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return paymentClient.confirm(paymentRequest).getBody();

        } catch (HttpClientErrorException e) {
            System.out.println(e);
            throw new PaymentException(e.getResponseBodyAs(PaymentErrorResponse.class));
        }
    }
}
