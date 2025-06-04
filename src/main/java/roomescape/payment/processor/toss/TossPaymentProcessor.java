package roomescape.payment.processor.toss;

import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

public class TossPaymentProcessor {

    private final String secretKey;
    private final RestClient restClient;

    public TossPaymentProcessor(
        final String secretKey,
        final RestClient restClient
    ) {
        this.secretKey = Base64.getEncoder().encodeToString((secretKey + ":base64").getBytes());
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponse processPayment(
        final TossPaymentConfirmRequest request
    ) {
        final String confirmUri = "/confirm";

        return restClient.post()
            .uri(confirmUri)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + secretKey)
            .body(request)
            .retrieve()
            .body(TossPaymentConfirmResponse.class);
    }
}
