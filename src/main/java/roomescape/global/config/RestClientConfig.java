package roomescape.global.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.restclient.PaymentWithRestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public PaymentWithRestClient paymentWithRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(3000));
        requestFactory.setReadTimeout(Duration.ofMillis(3000));
        return new PaymentWithRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments")
                        .requestFactory(requestFactory)
                        .build()
        );
    }
}
