package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import roomescape.payment.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossPaymentClient paymentRestClient() {
        return new TossPaymentClient(factory());
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory factory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setConnectionRequestTimeout(10_000);
        return factory;
    }
}
