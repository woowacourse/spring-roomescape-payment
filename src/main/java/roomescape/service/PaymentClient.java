package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.service.dto.response.PaymentResponse;
import roomescape.controller.dto.PaymentRequest;

@Component
public class PaymentClient {

    @Value("${payment.payment-approve-endpoint}")
    private String paymentApproveEndpoint;
    @Value("${payment.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(encodeSecretKey());
        HttpEntity<PaymentRequest> httpEntity = new HttpEntity<>(paymentRequest, headers);

        return restTemplate.postForEntity(
                paymentApproveEndpoint,
                httpEntity,
                PaymentResponse.class
        ).getBody();
    }

    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":") //TODO 빼기
                        .getBytes(StandardCharsets.UTF_8));
    }
}
