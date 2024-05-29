package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientConfig {
    @Value("${security.payment.url}")
    private String url;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
}
