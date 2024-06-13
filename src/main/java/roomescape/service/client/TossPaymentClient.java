package roomescape.service.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import roomescape.service.client.handler.TossPaymentErrorHandler;
import roomescape.service.dto.PaymentRequest;

@Component
public class TossPaymentClient implements PaymentClient {
    public static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestClient restClient;
    private final String authorizations;

    public TossPaymentClient(RestClient restClient, @Value("${toss-payment.test-secret-key}") String key) {
        this.restClient = restClient;
        this.authorizations = createAuthorizations(key);
    }


    private String createAuthorizations(String key) {
        String tossPaymentTestKey = key + ":";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(tossPaymentTestKey.getBytes(StandardCharsets.UTF_8));
        return AUTHORIZATION_PREFIX + new String(encodedBytes);
    }

    @Override
    public void requestPayment(PaymentRequest body) {
        TossPaymentErrorHandler errorHandler = new TossPaymentErrorHandler();

        restClient.post()
                .uri(TOSS_PAYMENTS_URL)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, authorizations)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, errorHandler)
                .onStatus(HttpStatusCode::is5xxServerError, errorHandler)
                .toBodilessEntity();
    }
}
