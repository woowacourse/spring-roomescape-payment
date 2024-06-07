package roomescape.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class TossRestClientConfig {

    @Bean
    public RestClient tossRestClient(RestTemplateBuilder builder) {
        RestTemplate tossRestTemplate = builder.setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(30, ChronoUnit.SECONDS))
                .uriTemplateHandler(new DefaultUriBuilderFactory("https://api.tosspayments.com/v1/payments"))
                .build();

        return RestClient.create(tossRestTemplate);
    }
}
