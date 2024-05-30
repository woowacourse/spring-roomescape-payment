package roomescape.service.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.config.TossPaymentConfigProperties;

@Component
public class TossPaymentRestClient {

    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final TossPaymentConfigProperties properties;
    private final String authorizationKey;

    public TossPaymentRestClient(TossPaymentConfigProperties properties) {
        this.properties = properties;
        this.authorizationKey = AUTHORIZATION_PREFIX + new String(Base64.getEncoder()
            .encode(properties.getTestSecretKey().getBytes(UTF_8)));
    }

    public RestClient build() {
        return RestClient.builder()
            .baseUrl(properties.getPaymentApprovalUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationKey)
            .build();
    }
}
