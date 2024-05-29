package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.service.dto.PaymentRequestDto;
import roomescape.service.errorhandler.PaymentErrorHandler;

@Service
public class PaymentService {

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TOSS_PAYMENTS_TEST_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final RestClient restClient;

    public PaymentService() {
        this.restClient = RestClient.builder()
            .baseUrl(TOSS_PAYMENTS_URL)
            .build();
    }

    public void pay(PaymentRequestDto dto) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((TOSS_PAYMENTS_TEST_KEY + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = AUTHORIZATION_PREFIX + new String(encodedBytes);

        restClient.post()
            .uri(TOSS_PAYMENTS_URL)
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", authorizations)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, new PaymentErrorHandler())
            .onStatus(HttpStatusCode::is5xxServerError, new PaymentErrorHandler())
            .toBodilessEntity();
    }
}
