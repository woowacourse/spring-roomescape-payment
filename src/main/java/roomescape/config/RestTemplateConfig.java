package roomescape.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.RestTemplateResponseExceptionHandler;

@Configuration
public class RestTemplateConfig {

    @Bean
    SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(1000);
        factory.setConnectTimeout(3000);
        return factory;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.requestFactory(this::requestFactory)
                .errorHandler(new RestTemplateResponseExceptionHandler())
                .build();
    }
}
