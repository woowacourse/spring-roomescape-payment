package roomescape.service.conponent;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TossClientWithRestClient implements PaymentWithRestClient {

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final String authorizations;
    private final RestClient restClient;

    public TossClientWithRestClient(@Value("${toss-payment.test-secret-key}") String key) {
        String tossPaymentTestKey = key + ":";
        this.restClient = RestClient.builder()
                .baseUrl(TOSS_PAYMENTS_URL)
                .build();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(tossPaymentTestKey.getBytes(StandardCharsets.UTF_8));
        authorizations = AUTHORIZATION_PREFIX + new String(encodedBytes);

    }

    @Override
    public RestClient getRestClient() {
        return restClient;
    }

    @Override
    public String getAuthorizations() {
        return authorizations;
    }

    @Override
    public String getPaymentServerURL() {
        return TOSS_PAYMENTS_URL;
    }

}
