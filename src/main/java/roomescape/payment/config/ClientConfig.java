package roomescape.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossApiClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossApiClient tossApiClient() {
        return new TossApiClient(RestClient.create().mutate().build());
    }
}
