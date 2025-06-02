package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("production")
public class RestClientConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
