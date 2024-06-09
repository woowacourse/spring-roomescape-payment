package roomescape.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.PaymentClient;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.config.TossPaymentSettings;

import java.time.Duration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClient paymentRestClient(final ResponseErrorHandler errorHandler,
                                           final TossPaymentSettings tossPaymentSettings
    ) {
        return new TossPaymentClient(factory(), errorHandler, tossPaymentSettings);
    }

    @Bean
    public ClientHttpRequestFactory factory() {
        final ClientHttpRequestFactorySettings factorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(3_000))
                .withConnectTimeout(Duration.ofSeconds(10_000));

        return ClientHttpRequestFactories.get(factorySettings);
    }
}
