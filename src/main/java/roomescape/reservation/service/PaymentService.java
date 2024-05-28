package roomescape.reservation.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String secretKey;

    public PaymentService(
            RestClient restClient,
            @Value("${payment.secret-key}") String secretKey
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        try {
            restClient.post()
                    .uri(new URI("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", authorizations)
                    .body(paymentConfirmRequest);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
