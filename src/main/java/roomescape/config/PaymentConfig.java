package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
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
    @Value("${payment.toss.base-url}")
    private String baseUrl;

    @Bean
    public PaymentClientFactory paymentClientFactory() {
        return new TossPaymentClientFactory();
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = paymentClientFactory().createPaymentClient(
                restClientBuilder
                        .baseUrl(baseUrl));
        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return proxyFactory.createClient(PaymentClient.class);
    }
}
