package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${toss.payments.base-url}")
    private String tossPaymentsBaseUrl;

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder()
                .baseUrl(tossPaymentsBaseUrl)
                .build();
    }
}
