package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaymentService {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final String secretKey;
    private final RestClient restClient;

    public PaymentService(@Value("${payment.secret-key}") String secretKey) {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
        this.secretKey = secretKey;
    }

    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        restClient.post()
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
