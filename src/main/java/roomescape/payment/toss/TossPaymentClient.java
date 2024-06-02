package roomescape.payment.toss;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;
import roomescape.payment.dto.PaymentConfirmRequest;

public class TossPaymentClient implements PaymentClient {

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_URI = "/v1/payments/confirm";
    private static final String AUTH_TYPE = "Basic ";
    public static final String AUTHORIZATION = "Authorization";

    private final RestClient restClient;

    public TossPaymentClient(String secretKey) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(1))
                .withReadTimeout(Duration.ofSeconds(3));

        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(AUTHORIZATION, createAuthorizationValue(secretKey))
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, TossPaymentErrorHandler::handle)
                .toBodilessEntity();
    }

    private String createAuthorizationValue(String secretKey) {
        byte[] encodedBytes = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String encodedSecretKey = new String(encodedBytes);

        return AUTH_TYPE + encodedSecretKey;
    }
}
