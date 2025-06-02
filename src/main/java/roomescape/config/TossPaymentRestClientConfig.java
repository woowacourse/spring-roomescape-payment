package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;
import roomescape.payment.toss.PaymentErrorHandler;
import roomescape.payment.toss.TossErrorMapper;
import roomescape.payment.toss.TossPaymentRestClient;
import roomescape.util.Base64Utils;

@Configuration
@Profile("prod")
public class TossPaymentRestClientConfig {

    private static final int CONNECTION_TIMEOUT_SECOND = 1;
    private static final int RESPONSE_TIMEOUT_SECOND = 2;

    @Bean
    public RestClient tossRestClient(
            PaymentErrorHandler paymentErrorHandler,
            @Value("${toss.base-url}") String baseUrl,
            @Value("${toss.secret-key}") String secretKey
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " + Base64Utils.encode(secretKey))
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(paymentErrorHandler)
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    @Bean
    public PaymentErrorHandler paymentErrorHandler(ObjectMapper objectMapper, TossErrorMapper tossErrorMapper) {
        return new PaymentErrorHandler(objectMapper, tossErrorMapper);
    }

    @Bean
    public PaymentClient tossPaymentRestClientWrapper(RestClient tossPaymentRestClient) {
        return new TossPaymentRestClient(tossPaymentRestClient);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECOND));
        requestFactory.setReadTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECOND));
        return requestFactory;
    }
}
