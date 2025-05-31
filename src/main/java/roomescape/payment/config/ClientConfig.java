package roomescape.payment.config;

import static roomescape.common.util.EncodingUtil.encodeBase64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${payment.secret.key}")
    private String secretKey;

    @Bean
    public RestClient tossPaymentRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Basic " + encodeBase64((secretKey + ":")))
                .build();
    }
}
