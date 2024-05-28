package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;

public class TossPaymentRestClient {

    private final RestClient restClient;

    public TossPaymentRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse pay(PaymentRequest request) {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        return restClient.post()
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
