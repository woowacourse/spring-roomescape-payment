package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.payment.processor.toss.TossPaymentProcessorErrorHandler;

@Configuration
public class RestClientConfig {

    private final ObjectMapper objectMapper;
    private final String secretKey;
    private final String confirmUrl;

    public RestClientConfig(
            @Value("${payment.toss.secret-key}") final String secretKey,
            @Value("${payment.toss.confirm-url}") final String confirmUrl,
            final ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
    }

    @Bean
    public TossPaymentProcessor tossPaymentProcessor() {
        final RestClient restClient = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(objectMapper))
                .build();

        return new TossPaymentProcessor(secretKey, restClient, confirmUrl);
    }
}
