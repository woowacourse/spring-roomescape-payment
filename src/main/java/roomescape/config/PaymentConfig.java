package roomescape.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public ClientHttpRequestFactory getTimeoutFactory() {
        SimpleClientHttpRequestFactory timeoutFactory = new SimpleClientHttpRequestFactory();
        timeoutFactory.setConnectTimeout(Duration.ofSeconds(5));
        timeoutFactory.setReadTimeout(Duration.ofSeconds(30));

        return timeoutFactory;
    }

    @Bean
    public PaymentClient getTossPaymentClient(ClientHttpRequestFactory timeoutFactory) {

        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .requestFactory(timeoutFactory)
                .build();

        return new TossPaymentClient(restClient);
    }
}
