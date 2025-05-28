package roomescape.payment.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossRestClient;

@Configuration
public class ClientConfig {
    
    @Bean
    public TossRestClient tossRestClient() {
        return new TossRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build()
        );
    }
}
