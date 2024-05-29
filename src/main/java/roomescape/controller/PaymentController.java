package roomescape.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.dto.PaymentApproveRequest;

@RestController
public class PaymentController {
    private static final String PAYMENT_APPROVE_ENDPOINT = "https://api.tosspayments.com/v1/payments/confirm";

    @Value("${payment.secret-key}")
    private String secretKey;
    private final RestTemplate restTemplate;

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<PaymentApproveResponse> approve(HeaderGenerator headerGenerator, PaymentApproveRequest paymentApproveRequest) {
        HttpHeaders headers = headerGenerator.generate();
        headers.setBasicAuth(encodeSecretKey());
        HttpEntity httpEntity = new HttpEntity(paymentApproveRequest, headers);

        return restTemplate.postForEntity(
                PAYMENT_APPROVE_ENDPOINT,
                httpEntity,
                PaymentApproveResponse.class
        );
    }

    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
