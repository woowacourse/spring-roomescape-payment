package roomescape.infrastructure.payment;

import java.time.Duration;
import java.util.Map;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.util.LogSaver;

@Configuration
public class PaymentConfig {

    public static final String TOSS = "toss";

    private final Map<String, PaymentProperty> paymentProperties;
    private final Map<String, Builder> builders;

    public PaymentConfig() {
        this.paymentProperties = Map.of(
                TOSS, new PaymentProperty(TOSS, 5, 31)
        );
        this.builders = Map.of(
                TOSS, createBuilder(TOSS)
        );
    }

    private ClientHttpRequestFactory createHttpRequestFactory(final PaymentProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(property.connectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(property.readTimeoutSeconds()));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private Builder createBuilder(String vendor) {
        PaymentProperty property = paymentProperties.get(vendor);

        return RestClient.builder()
                .requestFactory(createHttpRequestFactory(property));
    }

    @Bean
    public TossPaymentClient tossPayRestClient(LogSaver logSaver) {
        Builder tossBuilder = builders.get(TOSS);
        return new TossPaymentClient(tossBuilder.build(), logSaver);
    }
}
