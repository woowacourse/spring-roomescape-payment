package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.toss.TossPaymentClient;
import roomescape.payment.infrastructure.toss.TossPaymentResponseErrorHandler;

@Configuration
public class RestClientConfig {

    private final String confirmUrl;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public RestClientConfig(
            @Value("${payment.toss.confirm-url}") final String confirmUrl,
            @Value("${payment.toss.secret-key}") final String secretKey,
            final ObjectMapper objectMapper
    ) {
        this.confirmUrl = confirmUrl;
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PaymentClient paymentClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        return new TossPaymentClient(
                confirmUrl,
                secretKey,
                RestClient.builder()
                        .requestFactory(requestFactory)
                        .defaultStatusHandler(new TossPaymentResponseErrorHandler(objectMapper))
                        .baseUrl(confirmUrl)
                        .build()
        );
    }
}
