package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.domain.Payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PaymentRestClient {

    private final RestClient restClient;

    @Value("${TOSS-KEY}")
    private String tossKey;

    public PaymentRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Payment getPayment(PaymentProcessRequest request) {
        return restClient.post()
                .header("Authorization", makeEncodedPaymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Payment.class);
    }

    private String makeEncodedPaymentKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((tossKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
