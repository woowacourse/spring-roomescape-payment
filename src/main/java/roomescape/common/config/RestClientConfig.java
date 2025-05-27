package roomescape.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.RestClientResponseErrorHandler;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient createRestClient() {
        return RestClient.builder()
                .defaultStatusHandler(new RestClientResponseErrorHandler())
                .build();
    }
}
