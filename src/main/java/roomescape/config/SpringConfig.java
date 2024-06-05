package roomescape.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.ApiExceptionHandler;
import roomescape.service.reservation.pay.PaymentProperties;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({PaymentProperties.class})
public class SpringConfig {

    @Bean
    SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(30));
        factory.setConnectTimeout(Duration.ofSeconds(60));
        return factory;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(this::requestFactory)
                .errorHandler(new ApiExceptionHandler())
                .build();
    }
}
