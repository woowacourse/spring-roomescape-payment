package roomescape.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient getTossPaymentClient() {
        SimpleClientHttpRequestFactory timeoutFactory = new SimpleClientHttpRequestFactory();
        timeoutFactory.setConnectTimeout(Duration.ofSeconds(5));
        timeoutFactory.setReadTimeout(Duration.ofSeconds(30));

        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .requestFactory(timeoutFactory)
                .build();
        return new TossPaymentClient(restClient);
    }
}
