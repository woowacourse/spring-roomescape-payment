package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.toss.TossPaymentClient;
import roomescape.payment.toss.TossPaymentRestClient;
import roomescape.payment.toss.exception.PaymentErrorHandler;
import roomescape.payment.toss.exception.TossErrorMapper;
import roomescape.util.Base64Utils;

@Configuration
@Profile("!test")
public class TossPaymentRestClientConfig {

    private static final int CONNECTION_TIMEOUT_SECOND = 3;
    private static final int RESPONSE_TIMEOUT_SECOND = 35;

    @Bean
    public TossPaymentClient tossPaymentClient(
            ObjectMapper objectMapper,
            TossErrorMapper tossErrorMapper,
            @Value("${toss.base-url}") String baseUrl,
            @Value("${toss.secret-key}") String secretKey) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " + Base64Utils.encode(secretKey))
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(new PaymentErrorHandler(objectMapper, tossErrorMapper))
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
        return new TossPaymentRestClient(restClient);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECOND));
        requestFactory.setReadTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECOND));
        return requestFactory;
    }
}
