package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.PaymentRequest;

@Service
public class PaymentService {

    private static final String KEY_PREFIX = "Basic ";

    @Value("${payment.secret-key}")
    private static String PAYMENT_SECRET_KEY;

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void payment(PaymentRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", createAuthorizations())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }

    private String createAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((PAYMENT_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return KEY_PREFIX + new String(encodedBytes);
    }
}
