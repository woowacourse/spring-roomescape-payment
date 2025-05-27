package roomescape.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final String TOSS_PAYMENT_API = "https://api.tosspayments.com";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(TOSS_PAYMENT_API)
                .build();
    }
}
