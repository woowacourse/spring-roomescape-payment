package roomescape.common.config;

import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;
import roomescape.infrastructure.payment.toss.exception.PaymentExceptionHandler;

@Configuration
public class PaymentConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    @Bean
    public TossPaymentWithRestClient tossPaymentWithRestClient(RestClient.Builder builder) {
        return new TossPaymentWithRestClient(builder
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodeSecretKey())
                .defaultStatusHandler(new PaymentExceptionHandler())
                .requestFactory(createRequestFactory())
                .build());
    }

    private static SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        return requestFactory;
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
