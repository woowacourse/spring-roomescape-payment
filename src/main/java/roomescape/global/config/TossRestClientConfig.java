package roomescape.global.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class TossRestClientConfig {

    private final String secretKey;
    private final String baseUrl;

    public TossRestClientConfig(@Value("${toss.payment.secret-key}") String secretKey, @Value("${toss.payment.base-url}") String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    @Bean
    public RestClient restClient() {
        String basicAuthValue = encodeBasicAuth(secretKey);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", basicAuthValue)
                .defaultHeader("Content-Type", APPLICATION_JSON_VALUE)
                .build();
    }

    private String encodeBasicAuth(String secretKey) {
        String auth = secretKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}
