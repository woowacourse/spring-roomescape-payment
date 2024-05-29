package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.PaymentErrorHandler;

@Configuration
public class ClientConfig {

    @Bean
    public ResponseErrorHandler errorHandler() {
        return new PaymentErrorHandler();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }
}
