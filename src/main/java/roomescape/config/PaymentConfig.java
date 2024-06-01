package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.client.PaymentClient;
import roomescape.client.PaymentClientFactory;
import roomescape.client.TossPaymentClientFactory;

@Configuration
public class PaymentConfig {
    @Bean
    public PaymentClientFactory paymentClientFactory() {
        return new TossPaymentClientFactory();
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = paymentClientFactory().createPaymentClient(
                restClientBuilder
                        .baseUrl("https://api.tosspayments.com/v1/payments"));
        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return proxyFactory.createClient(PaymentClient.class);
    }
}
