package roomescape.payment;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.reservation.controller.PaymentConfirmRequest;
import roomescape.reservation.controller.PaymentConfirmResponse;

@Service
public class PaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String AUTHORIZATION_DELIMITER = ":";

    private final RestClient restClient;

    public PaymentService(final Builder restClientBuilder) {
        restClient = restClientBuilder
                .defaultStatusHandler(new PaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .build();
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest request) {
        return restClient.post()
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private String getAuthorization() {
        return AUTHORIZATION_PREFIX + encodeToBase64((SECRET_KEY + AUTHORIZATION_DELIMITER));
    }

    private String encodeToBase64(final String value) {
        final Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
