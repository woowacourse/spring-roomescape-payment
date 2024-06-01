package roomescape.service.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.service.config.TossPaymentConfigProperties;

@Component
public class TossPaymentRestClient {

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final Duration CONNECT_TIMEOUT_DURATION = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT_DURATION = Duration.ofSeconds(30);

    private final TossPaymentConfigProperties properties;
    private final String authorizationKey;
    private final RestClient.Builder builder;

    public TossPaymentRestClient(TossPaymentConfigProperties properties, Builder builder) {
        this.properties = properties;
        this.authorizationKey = AUTHORIZATION_PREFIX + new String(Base64.getEncoder()
            .encode(properties.getTestSecretKey().getBytes(UTF_8)));
        this.builder = builder;
    }

    public RestClient build() {
        return builder.baseUrl(properties.getPaymentApprovalUrl())
            .requestFactory(timeoutFactory())
            .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationKey)
            .build();
    }

    private ClientHttpRequestFactory timeoutFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_DURATION);
        factory.setReadTimeout(READ_TIMEOUT_DURATION);
        return factory;
    }
}
