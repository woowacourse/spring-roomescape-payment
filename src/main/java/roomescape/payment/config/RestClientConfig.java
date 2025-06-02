package roomescape.payment.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.TossPaymentClient;

@Configuration
public class RestClientConfig {

    private final String confirmUrl;
    private final String secretKey;

    public RestClientConfig(
            @Value("${payment.toss.confirm-url}") final String confirmUrl,
            @Value("${payment.toss.secret-key}") final String secretKey
    ) {
        this.confirmUrl = confirmUrl;
        this.secretKey = secretKey;
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
                        .baseUrl(confirmUrl)
                        .build()
        );
    }
}
