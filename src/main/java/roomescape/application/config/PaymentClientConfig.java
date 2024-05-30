package roomescape.application.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import roomescape.application.payment.PaymentErrorHandler;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    @Bean
    public ResponseErrorHandler errorHandler() {
        return new PaymentErrorHandler();
    }

    @Bean
    public RestClient.Builder restClientBuilder(PaymentClientProperties properties,
                                                RestTemplateBuilder builder) {
        RestTemplate template = builder
                .setConnectTimeout(Duration.ofSeconds(3L))
                .setReadTimeout(Duration.ofSeconds(30L))
                .build();
        return RestClient.builder(template)
                .baseUrl(properties.getUrl());
    }
}
