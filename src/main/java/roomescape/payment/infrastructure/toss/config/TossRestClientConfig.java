package roomescape.payment.infrastructure.toss.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.common.provider.RestClientProvider;

@Configuration
@AllArgsConstructor
public class TossRestClientConfig {
    private final TossApiProperties tossApiProperties;

    @Bean(name = "tossApiRestClient")
    public RestClient tossRestClient() {
        return RestClientProvider.createRestClient(tossApiProperties);
    }
}
