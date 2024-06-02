package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.HeaderGenerator;
import roomescape.controller.PaymentApproveResponse;
import roomescape.controller.dto.PaymentApproveRequest;

@Service
public class PaymentService {
    private static final String PAYMENT_APPROVE_ENDPOINT = "https://api.tosspayments.com/v1/payments/confirm";

    @Value("${payment.secret-key}")
    private String secretKey;
    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentApproveResponse pay(HeaderGenerator headerGenerator, PaymentApproveRequest paymentApproveRequest) {
        HttpHeaders headers = headerGenerator.generate();
        headers.setBasicAuth(encodeSecretKey());
        HttpEntity<PaymentApproveRequest> httpEntity = new HttpEntity<>(paymentApproveRequest, headers);

        return restTemplate.postForEntity(
                PAYMENT_APPROVE_ENDPOINT,
                httpEntity,
                PaymentApproveResponse.class
        ).getBody();
    }

    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
