package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.global.util.Encoder;
import roomescape.payment.PaymentErrorResponse;
import roomescape.payment.PaymentRequest;
import roomescape.payment.PaymentResponse;
import roomescape.payment.TossPaymentProperties;
import roomescape.payment.exception.PaymentException;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentClient paymentClient;

    private final Encoder encoder;
    private final TossPaymentProperties tossPaymentProperties;

    public PaymentService(PaymentClient paymentClient, Encoder encoder, TossPaymentProperties tossPaymentProperties) {
        this.paymentClient = paymentClient;
        this.encoder = encoder;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            String encodeKey = encoder.encode(tossPaymentProperties.getSecretKey());
            return paymentClient.confirm(paymentRequest,encodeKey).getBody();

        } catch (HttpClientErrorException e) {
            System.out.println(e);
            throw new PaymentException(e.getResponseBodyAs(PaymentErrorResponse.class));
        }
    }
}
