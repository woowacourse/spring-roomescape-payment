package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import roomescape.payment.PaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentRestClient() {
        return new PaymentClient(factory());
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory factory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setConnectionRequestTimeout(10_000);
        return factory;
    }
}
