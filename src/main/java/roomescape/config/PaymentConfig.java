package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.exception.TossPayErrorHandler;
import roomescape.payment.client.TossPayRestClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private static final String BASIC = "Basic ";

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public PaymentClientBuilders builders() {
        return new PaymentClientBuilders(createBuilders());
    }

    private Map<String, Builder> createBuilders() {
        Map<String, Builder> builders = new HashMap<>();
        paymentProperties.getProperties().keySet()
                .forEach(vendor -> builders.put(vendor, createBuilder(vendor)));
        return builders;
    }

    private Builder createBuilder(String vendor) {
        PaymentProperty property = paymentProperties.get(vendor);

        return RestClient.builder()
                .requestFactory(createHttpRequestFactory(property))
                .defaultHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(property))
                .baseUrl(property.baseUrl());
    }

    private ClientHttpRequestFactory createHttpRequestFactory(PaymentProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(property.connectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(property.readTimeoutSeconds()));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String createBasicAuthorization(PaymentProperty property) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((property.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return BASIC + new String(encodedBytes);
    }

    @Bean
    public TossPayRestClient tossPayRestClient() {
        Builder tossBuilder = builders().get("toss")
                .defaultStatusHandler(new TossPayErrorHandler());
        return new TossPayRestClient(tossBuilder.build());
    }
}
