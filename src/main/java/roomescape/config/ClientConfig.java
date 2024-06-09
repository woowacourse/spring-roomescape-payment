package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import roomescape.payment.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossPaymentClient paymentRestClient() {
        return new TossPaymentClient(factory());
    }

    @Bean
    public SimpleClientHttpRequestFactory factory() {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1_000); // 1 seconds for connection timeout
        factory.setReadTimeout(3_000);    // 3 seconds for read timeout
        return factory;
    }
}
