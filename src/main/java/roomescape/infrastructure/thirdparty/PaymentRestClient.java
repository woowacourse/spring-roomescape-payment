package roomescape.infrastructure.thirdparty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PaymentRestClient {

    private final RestClient restClient;

    @Value("${toss.payment.api.key}")
    private String tossKey;

    public PaymentRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<String> getPaymentResponse(PaymentProcessRequest request) {
        return restClient.post()
                .header("Authorization", makeEncodedPaymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(String.class);
    }

    private String makeEncodedPaymentKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((tossKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
