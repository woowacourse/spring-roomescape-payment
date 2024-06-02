package roomescape.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.ApiExceptionHandler;
import roomescape.service.reservation.pay.PaymentProperties;
import roomescape.service.reservation.pay.PaymentService;

@Configuration
@EnableConfigurationProperties({PaymentProperties.class})
public class SpringConfig {

    @Autowired
    private PaymentProperties paymentProperties;

    @Bean
    SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(1000);
        factory.setConnectTimeout(3000);
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
