package roomescape.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Configuration
public class RestClientConfig {

    private final String secretKey;

    public RestClientConfig(@Value("${toss.payment.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public RestClient restClient() {
        String basicAuthValue = encodeBasicAuth(secretKey);

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
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
