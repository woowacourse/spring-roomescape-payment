package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.PaymentClient;
import roomescape.payment.TossPaymentClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClient paymentRestClient(final ResponseErrorHandler errorHandler,
                                           @Value("${payments.toss.secret-key}") final String secretKey,
                                           @Value("${payments.toss.password}") final String password,
                                           @Value("${payments.toss.host-name}") final String hostName,
                                           @Value("${payments.toss.payment-api}") final String paymentApi
    ) {
        return new TossPaymentClient(factory(), errorHandler, secretKey, password, hostName, paymentApi);
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory factory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setConnectionRequestTimeout(10_000);
        return factory;
    }
}
