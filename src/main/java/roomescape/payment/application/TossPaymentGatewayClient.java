package roomescape.payment.application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;

@Component
public class TossPaymentGatewayClient {

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_ENDPOINT = "/v1/payments/confirm";

    private final RestClient restClient;

    public TossPaymentGatewayClient() {
        this.restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(AUTHORIZATION, encodeSecretKey("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"))
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();
    }

    private String encodeSecretKey(final String secretKey) {
        final String raw = secretKey + ":";
        final String encoded = Base64.getEncoder()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    public TossConfirmResponse processPaymentConfirm(final TossConfirmRequest request) {
        return restClient.post()
            .uri(CONFIRM_ENDPOINT)
            .accept(APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(TossConfirmResponse.class);
    }
}
