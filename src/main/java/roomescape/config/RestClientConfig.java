package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentRestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public ClientHttpRequestFactory getTimeoutFactory() {
        SimpleClientHttpRequestFactory timeoutFactory = new SimpleClientHttpRequestFactory();
        timeoutFactory.setConnectTimeout(Duration.ofSeconds(3));
        timeoutFactory.setReadTimeout(Duration.ofSeconds(30));

        return timeoutFactory;
    }

    @Bean
    public PaymentRestClient paymentRestClient(ClientHttpRequestFactory factory) {
        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments")
                        .requestFactory(factory)
                        .build(),
                new ObjectMapper()
        );
    }
}
