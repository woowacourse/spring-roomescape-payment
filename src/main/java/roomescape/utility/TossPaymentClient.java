package roomescape.utility;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.client.RestClient;
import roomescape.dto.business.PaymentResult;

public class TossPaymentClient implements PaymentClient {

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentClient(
            RestClient restClient,
            String secretKey
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    @Override
    public PaymentResult pay(String paymentKey, String orderId, int amount) {
        return restClient.get()
                .uri(PAYMENT_CONFIRM_URL)
                .header("Authorization", createAuthHeaderConcise())
                .retrieve()
                .body(PaymentResult.class);
    }

    public String createAuthHeaderConcise() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
