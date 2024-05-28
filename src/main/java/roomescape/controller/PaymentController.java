package roomescape.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentController {
    private static final String PAYMENT_APPROVE_ENDPOINT = "https://api.tosspayments.com/v1/payments/confirm";

    @Value("${payment.secret-key}")
    private String secretKey;
    private final RestTemplate restTemplate;

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<PaymentApproveResponse> approve(PaymentApproveRequest paymentApproveRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(encodeSecretKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

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
