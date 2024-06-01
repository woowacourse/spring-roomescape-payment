package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.client.PaymentClient;
import roomescape.client.PaymentClientFactory;

@Configuration
public class PaymentConfig {
    private final PaymentClientFactory paymentClientFactory;

    public PaymentConfig(PaymentClientFactory paymentClientFactory) {
        this.paymentClientFactory = paymentClientFactory;
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = paymentClientFactory.createPaymentClient(restClientBuilder);
        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return proxyFactory.createClient(PaymentClient.class);
    }
}
