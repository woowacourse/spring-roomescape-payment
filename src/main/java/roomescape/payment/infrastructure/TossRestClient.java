package roomescape.payment.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

@RequiredArgsConstructor
@Slf4j
public class TossRestClient {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String AUTH_HEADER_VALUE = "Basic " +
            Base64.getEncoder().encodeToString((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));


    private final RestClient restClient;

    public TossPaymentResponse confirm(TossPaymentRequest tossPaymentRequest) {
        final TossPaymentResponse authorization = restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", AUTH_HEADER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(tossPaymentRequest)
                .retrieve()
                .body(TossPaymentResponse.class);

        log.info("Confirmed payment: {}", authorization);
        return authorization;
    }
}
