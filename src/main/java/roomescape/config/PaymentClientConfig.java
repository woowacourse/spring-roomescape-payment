package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.PaymentClient;
import roomescape.payment.TossPaymentClient;

import java.time.Duration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClient paymentRestClient(final ResponseErrorHandler errorHandler,
                                           @Value("${payments.toss.secret-key}") final String secretKey,
                                           @Value("${payments.toss.password}") final String password,
                                           @Value("${payments.toss.host-name}") final String hostName,
                                           @Value("${payments.toss.create-payment-api}") final String createApi,
                                           @Value("${payments.toss.cancel-payment-api}") final String cancelApi
    ) {
        return new TossPaymentClient(factory(), errorHandler, secretKey, password, hostName, createApi, cancelApi);
    }

    @Bean
    public ClientHttpRequestFactory factory() {
        final ClientHttpRequestFactorySettings factorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(3_000))
                .withConnectTimeout(Duration.ofSeconds(10_000));

        return ClientHttpRequestFactories.get(factorySettings);
    }
}
