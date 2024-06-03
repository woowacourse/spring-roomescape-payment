package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {

    @Bean
    public RestClient PaymentRestClient() {
        return RestClient.builder().baseUrl("https://api.tosspayments.com").build();
    }
}
