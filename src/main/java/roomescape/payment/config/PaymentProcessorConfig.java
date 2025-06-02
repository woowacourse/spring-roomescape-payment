package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.payment.processor.toss.TossPaymentProcessorErrorHandler;

@Configuration
public class PaymentProcessorConfig {

    private final ObjectMapper objectMapper;
    private final String secretKey;

    public PaymentProcessorConfig(
            @Value("${payment.toss.secret-key}") final String secretKey,
            final ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
    }

    @Bean
    public TossPaymentProcessor tossPaymentProcessor() {
        final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(objectMapper))
                .build();

        return new TossPaymentProcessor(secretKey, restClient);
    }
}
