package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentProperty;
import roomescape.payment.infra.PaymentWithRestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(PaymentProperty.class)
public class PaymentRestClientConfiguration {

    private final PaymentProperty paymentProperty;

    public PaymentRestClientConfiguration(PaymentProperty paymentProperty) {
        this.paymentProperty = paymentProperty;
    }

    @Bean
    public PaymentWithRestClient paymentWithRestClient() {
        RestClient.Builder builder = createBuilder(paymentProperty);
        RestClient restClient = builder.build();
        return new PaymentWithRestClient(restClient);
    }

    private RestClient.Builder createBuilder(PaymentProperty paymentProperty) {
        return RestClient.builder()
                .requestFactory(getClientFactory(paymentProperty))
                .baseUrl(paymentProperty.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + paymentProperty.getSecretKey());
    }

    private ClientHttpRequestFactory getClientFactory(PaymentProperty paymentProperty) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(paymentProperty.getReadTimeout()))
                .withConnectTimeout(Duration.ofSeconds(paymentProperty.getConnectionTimeout()));
        return ClientHttpRequestFactories.get(settings);
    }
}
