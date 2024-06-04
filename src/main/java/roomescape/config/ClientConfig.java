package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.infrastructure.PaymentClient;

@Configuration
public class ClientConfig {
    @Bean
    public PaymentClient getPaymentClient(Builder builder) {
        return new PaymentClient(builder
                .baseUrl("https://api.tosspayments.com/")
                .build());
    }

    @Bean
    public Builder restClient() {
        return RestClient.builder();
    }
}
