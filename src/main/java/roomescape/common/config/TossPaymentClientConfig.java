package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentClientConfig {

    private final String secretKey;

    // ✅ 생성자 주입은 클래스 생성자에서 수행
    public TossPaymentClientConfig(@Value("${toss.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public RestClient tossRestClient() {
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
