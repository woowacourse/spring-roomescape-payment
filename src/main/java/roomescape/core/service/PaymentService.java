package roomescape.core.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.infrastructure.PaymentSecretKeyEncoder;

@Service
public class PaymentService {
    private final RestClient restClient;
    private final PaymentSecretKeyEncoder paymentSecretKeyEncoder;

    public PaymentService(RestClient restClient, PaymentSecretKeyEncoder paymentSecretKeyEncoder) {
        this.restClient = restClient;
        this.paymentSecretKeyEncoder = paymentSecretKeyEncoder;
    }

    public PaymentConfirmResponse confirmPayment(final PaymentConfirmRequest request) {
        final String authorizations = paymentSecretKeyEncoder.getEncodedSecretKey();

        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }
}
