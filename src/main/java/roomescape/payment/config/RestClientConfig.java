package roomescape.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.common.RestClientResponseErrorHandler;
import roomescape.payment.processor.toss.TossPaymentProcessor;

@Configuration
public class RestClientConfig {

    private final String secretKey;

    public RestClientConfig(
        @Value("${payment.toss.secret-key}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    @Bean
    public TossPaymentProcessor tossPaymentProcessor() {
        RestClient restClient = RestClient.builder()
            .defaultStatusHandler(new RestClientResponseErrorHandler())
            .build();

        return new TossPaymentProcessor(secretKey, restClient);
    }
}
