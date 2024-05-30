package roomescape.infrastructure.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

@Component
@Profile("!local")
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    @Value("${security.payment.secret-key}")
    private String WIDGET_SECRET_KEY;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(PaymentRequest request) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        return restClient.post()
            .uri("/v1/payments/confirm")
            .contentType(APPLICATION_JSON)
            .header("Authorization", authorizations)
            .body(request)
            .retrieve()
            .body(Payment.class);
    }
}
