package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl("https://api.tosspayments.com").build();
    }
}
