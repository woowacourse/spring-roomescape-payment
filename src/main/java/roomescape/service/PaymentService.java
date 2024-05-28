package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.service.dto.PaymentRequest;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    public PaymentService() {
        this.restClient = RestClient
                .builder()
                .baseUrl("https://api.tosspayments.com")
                .build();
    }

    public void requestApproval(PaymentRequest request) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder
                .encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(request)
                .retrieve();
    }
}
