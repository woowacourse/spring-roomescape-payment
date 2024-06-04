package roomescape.infrastructure.payment;

import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public class PaymentRestClientBuilder {
    private static final String AUTHORIZATION_HEADER = "Basic ";

    private final PaymentProperties properties;

    public PaymentRestClientBuilder(PaymentProperties properties) {
        this.properties = properties;
    }

    public RestClient.Builder generate(String clientName) {
        PaymentProperties.PaymentProperty paymentProperty = properties.getProperty(clientName);

        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory(paymentProperty))
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorizationHeader(paymentProperty));
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(PaymentProperties.PaymentProperty paymentProperty) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(paymentProperty.connectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(paymentProperty.readTimeoutSeconds()));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String createAuthorizationHeader(PaymentProperties.PaymentProperty paymentProperty) {
        return AUTHORIZATION_HEADER + Base64.getEncoder()
                .encodeToString((paymentProperty.secretKey() + paymentProperty.password()).getBytes());
    }

    public PaymentProperties getProperties() {
        return properties;
    }
}
