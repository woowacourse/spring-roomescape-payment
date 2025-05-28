package roomescape.payment.processor.toss;

import java.util.Base64;
import org.springframework.http.MediaType;
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
        final String uri = "https://api.tosspayments.com/v1/payments/confirm";

        return restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + secretKey)
                .body(new TossPaymentConfirmRequest(request.amount(), request.orderId(), request.paymentKey()))
                .retrieve()
                .body(TossPaymentConfirmResponse.class);
    }
}
