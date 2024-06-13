package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.dto.PaymentRequest;
import roomescape.service.dto.response.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentCancelRequestBody;
import roomescape.service.dto.response.PaymentResponse;

@Component
public class PaymentClient {

    @Value("${payment.payment-approve-endpoint}")
    private String paymentApproveEndpoint;
    @Value("${payment.payment-cancel-endpoint}")
    private String paymentCancelEndpoint;
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

    public void cancel(PaymentCancelRequest cancelRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(encodeSecretKey());
        HttpEntity<PaymentCancelRequestBody> httpEntity = new HttpEntity<>(cancelRequest.cancelRequestBody(), headers);

        restTemplate.postForEntity(
                String.format(paymentCancelEndpoint, cancelRequest.paymentKey()),
                httpEntity,
                PaymentResponse.class
        );
    }

    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
