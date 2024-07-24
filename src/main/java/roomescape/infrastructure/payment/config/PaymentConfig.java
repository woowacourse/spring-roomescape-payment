package roomescape.infrastructure.payment.config;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.util.LogSaver;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    public static final String TOSS = "toss";

    private final Map<String, PaymentProperty> paymentProperties;
    private final Map<String, Builder> builders;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = createProperties(paymentProperties);
        this.builders = createBuilders(paymentProperties);
    }

    private Map<String, PaymentProperty> createProperties(final PaymentProperties paymentProperties) {
        return paymentProperties.properties().stream()
                .collect(Collectors.toMap(
                        PaymentProperty::vendor,
                        paymentProperty -> paymentProperty));
    }

    private Map<String, Builder> createBuilders(final PaymentProperties paymentProperties) {
        return paymentProperties.properties().stream()
                .collect(Collectors.toMap(
                        PaymentProperty::vendor,
                        paymentProperty -> createBuilder(paymentProperty.vendor())));
    }

    private ClientHttpRequestFactory createHttpRequestFactory(final PaymentProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(property.connectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(property.readTimeoutSeconds()));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    public Builder createBuilder(String vendor) {
        PaymentProperty property = paymentProperties.get(vendor);
        String authorizationKey = property.secretKey() + ":";

        return RestClient.builder()
                .requestFactory(createHttpRequestFactory(property))
                .baseUrl(property.url())
                .defaultHeader("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(authorizationKey.getBytes()));
    }

    @Bean
    public TossPaymentClient tossPayRestClient(LogSaver logSaver) {
        Builder tossBuilder = builders.get(TOSS);
        return new TossPaymentClient(tossBuilder.build(), logSaver);
    }
}
