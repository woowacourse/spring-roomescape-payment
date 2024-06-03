package roomescape.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.TossPaymentProperties;

import java.util.Base64;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentConfig {

    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentConfig(final TossPaymentProperties tossPaymentProperties) {
        this.tossPaymentProperties = tossPaymentProperties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", authorizationHeader())
                .build();
    }

    private String authorizationHeader() {
        String secretKey = tossPaymentProperties.secretKey() + ":";
        String credentials = Base64.getEncoder()
                .encodeToString((secretKey).getBytes());

        return "Basic " + credentials;
    }
}
