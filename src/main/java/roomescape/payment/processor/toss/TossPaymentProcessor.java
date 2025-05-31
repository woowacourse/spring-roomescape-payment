package roomescape.payment.processor.toss;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class TossPaymentProcessor {

    private final String secretKey;
    private final RestClient restClient;
    private final String confirmUrl;

    public TossPaymentProcessor(
            final String secretKey,
            final RestClient restClient,
            final String confirmUrl
    ) {
        this.secretKey = Base64.getEncoder().encodeToString((secretKey + ":base64").getBytes());
        this.restClient = restClient;
        this.confirmUrl = confirmUrl;
    }

    public TossPaymentConfirmResponse processPayment(
            final TossPaymentConfirmRequest request
    ) {
        return restClient.post()
                .uri(confirmUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + secretKey)
                .body(request)
                .retrieve()
                .body(TossPaymentConfirmResponse.class);
    }
}
