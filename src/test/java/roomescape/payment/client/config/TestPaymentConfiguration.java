package roomescape.payment.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class TestPaymentConfiguration {

    @Value("${payment.api.base-url}")
    private String URL;

    @Bean
    @Primary
    public RestClient getRestClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
