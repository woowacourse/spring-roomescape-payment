package roomescape.global.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;
import roomescape.payment.PaymentWithRestClient;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public PaymentClient paymentWithRestClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(10L))
                .withConnectTimeout(Duration.ofSeconds(10L));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
        return new PaymentWithRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments")
                        .requestFactory(requestFactory)
                        .build()
        );
    }
}
