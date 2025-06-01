package roomescape.infrastructure.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.thirdparty.filter.TossPaymentErrorResponseFilter;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@Component
public class TossPaymentRestClient {

    private final RestClient restClient;
    private final String tossKey;
    private final ObjectMapper objectMapper;

    public TossPaymentRestClient(
            @Value("${toss.payment.api.base-url}") String baseUrl,
            @Value("${toss.payment.api.key}") String tossKey,
            ObjectMapper objectMapper,
            RestClient.Builder restClientBuilder
    ) {
        this.tossKey = tossKey;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().set("Authorization", makeEncodedPaymentKey());
                    return execution.execute(request, body);
                })
                .defaultStatusHandler(new TossPaymentErrorResponseFilter(objectMapper))
                .build();
    }

    public ResponseEntity<String> getPaymentResponse(PaymentProcessRequest request) {
        return restClient.post()
                .uri("/payments/confirm")
                .body(request)
                .retrieve()
                .toEntity(String.class);
    }

    private String makeEncodedPaymentKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((tossKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
