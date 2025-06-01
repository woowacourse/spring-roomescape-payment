package roomescape.payment.infrastructure.toss.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        String credentials = tossApiProperties.getSecretKey() + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return RestClientProvider.createRestClient(tossApiProperties)
                .defaultHeader("Authorization", "Basic " + encoded)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
