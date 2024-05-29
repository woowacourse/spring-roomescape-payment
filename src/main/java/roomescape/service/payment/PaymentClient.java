package roomescape.service.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.service.payment.dto.PaymentConfirmRequest;
import roomescape.service.payment.dto.PaymentConfirmResponse;

public class PaymentClient {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";

    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((SECRET_KEY + BASIC_DELIMITER).getBytes(StandardCharsets.UTF_8));
            String authorizations = AUTH_HEADER_PREFIX + new String(encodedBytes);

            return restClient.method(HttpMethod.POST)
                    .uri(BASE_URL + "/confirm")
                    .header(HttpHeaders.AUTHORIZATION, authorizations)
                    .body(request)
                    .retrieve()
                    .body(PaymentConfirmResponse.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
