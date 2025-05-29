package roomescape.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentConfig {

    private static final String TOSS_API_BASE_URL = "https://api.tosspayments.com";

    @Bean
    public RestClient restClient(final RestClient.Builder builder) {
        return builder.baseUrl(TOSS_API_BASE_URL).build();
    }

    @Bean
    public TossPaymentProvider tossPaymentProvider(final RestClient restClient) {
        return new TossPaymentProvider(restClient);
    }
}
