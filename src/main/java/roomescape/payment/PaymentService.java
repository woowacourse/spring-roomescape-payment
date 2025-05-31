package roomescape.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Service
public class PaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final RestClient restClient;

    public PaymentService(final Builder restClientBuilder) {
        restClient = restClientBuilder
                .defaultStatusHandler(new PaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest request) {
        return restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private String getAuthorization() {
        return "Basic " + encodeToBase64((SECRET_KEY + ":"));
    }

    private String encodeToBase64(final String value) {
        final Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
