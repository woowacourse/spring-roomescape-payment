package roomescape.payment.application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.config.TossPaymentProperties;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentGatewayClient {

    private static final String CONFIRM_ENDPOINT = "/v1/payments/confirm";

    private final RestClient restClient;

    public TossPaymentGatewayClient(
        final RestClient.Builder restClientBuilder,
        final ObjectMapper objectMapper,
        final TossPaymentProperties properties
    ) {
        this.restClient = restClientBuilder
            .requestFactory(createTimeoutFactory(properties))
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(AUTHORIZATION, encodeSecretKey(properties.getSecretKey()))
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .defaultStatusHandler(new TossPaymentErrorHandler(objectMapper))
            .build();
    }

    private SimpleClientHttpRequestFactory createTimeoutFactory(final TossPaymentProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return factory;
    }

    private String encodeSecretKey(final String secretKey) {
        String raw = secretKey + ":";
        String encoded = Base64.getEncoder()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    public TossConfirmResponse processPaymentConfirm(final TossConfirmRequest request) {
        return restClient.post()
            .uri(CONFIRM_ENDPOINT)
            .accept(APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(TossConfirmResponse.class);  // NOTE. 역직렬화 로직에 대한 책임을 분리하여 단위테스트를 작성할 수 있으나, SRP를 고려하여 하지 않음 파랑에게 여쭤보기
    }
}
