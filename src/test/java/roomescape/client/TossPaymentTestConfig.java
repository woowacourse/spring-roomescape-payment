package roomescape.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import roomescape.common.util.TokenCookieManager;

@TestConfiguration
public class TossPaymentTestConfig {

    private final String secretKey;

    public TossPaymentTestConfig(@Value("${WRONG_SECRET_KEY}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    @Primary
    public RestClient tossClient() {
//        String secretKey = "WRONG_SECRET_KEY";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public TokenCookieManager tokenCookieManager() {
        return new TokenCookieManager();
    }
}
