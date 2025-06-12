package roomescape.application.payment.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class TossPaymentConfig {

    private static final String TOSS_PAYMENT_SERVER_URL = "https://api.tosspayments.com/v1/payments";

    @Bean
    public RestClient tossPaymentRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        return RestClient.builder()
                .baseUrl(TOSS_PAYMENT_SERVER_URL)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public String tossPaymentSecretKey(@Value("${toss-payment.secret-key}") final String secretKey) {
        return buildSecretKey(secretKey);
    }

    private String buildSecretKey(final String secretKey) {
        return secretKey + ":";
    }
}
