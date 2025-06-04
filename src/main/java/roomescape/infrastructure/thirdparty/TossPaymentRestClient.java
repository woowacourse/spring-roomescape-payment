package roomescape.infrastructure.thirdparty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.infrastructure.thirdparty.filter.TossPaymentErrorResponseFilter;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@Component
public class TossPaymentRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String tossKey;

    public TossPaymentRestClient(
            @Value("${toss.payment.api.base-url}") String baseUrl,
            @Value("${toss.payment.api.key}") String tossKey,
            @Qualifier("TossRestTemplate") RestTemplate restTemplate,
            TossPaymentErrorResponseFilter errorHandler
    ) {
        this.baseUrl = baseUrl;
        this.tossKey = tossKey;
        this.restTemplate = restTemplate;
        this.restTemplate.setErrorHandler(errorHandler);
    }

    public ResponseEntity<String> getPaymentResponse(PaymentProcessRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", makeEncodedPaymentKey());

        HttpEntity<PaymentProcessRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForEntity(baseUrl + "/payments/confirm", entity, String.class);
    }

    private String makeEncodedPaymentKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((tossKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    // For testing purposes only
    RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
