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
        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
