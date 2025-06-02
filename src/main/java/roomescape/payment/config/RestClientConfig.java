package roomescape.payment.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.TossPaymentClient;

@Configuration
public class RestClientConfig {

    @Bean
    public PaymentClient paymentClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(2));

        return new TossPaymentClient(
                RestClient.builder()
                        .requestFactory(requestFactory)
                        .build()
        );
    }
}
