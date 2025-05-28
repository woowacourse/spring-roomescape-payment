package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentClientConfig {
    @Bean
    public RestClient tossRestClient() {
        String secretKey = "test_sk_MY_SECRET_KEY";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
