package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(PaymentProperties.class)
public class RestClientBuilders {
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String SECRET_KEY_SUFFIX = ":";

    private final PaymentProperties paymentProperties;
    private final Map<String, RestClient.Builder> builders;

    public RestClientBuilders(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
        this.builders = initBuilders(paymentProperties);
    }

    private Map<String, RestClient.Builder> initBuilders(PaymentProperties paymentProperties) {
        return paymentProperties.getNames()
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        this::createBuilder
                ));
    }

    private RestClient.Builder createBuilder(String name) {
        PaymentProperty property = paymentProperties.getProperty(name);
        return RestClient.builder()
                .requestFactory(clientFactory(property))
                .baseUrl(property.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, initializeAuthorizationKey(property));
    }


    private ClientHttpRequestFactory clientFactory(PaymentProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(property.getConnectionTimeout()))
                .withReadTimeout(Duration.ofSeconds(property.getReadTimeout()));
        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory::new, settings);
    }

    private String initializeAuthorizationKey(PaymentProperty property) {
        Encoder encoder = Base64.getEncoder();
        String secretKey = property.getSecretKey();
        byte[] encodedSecretKey = encoder.encode((secretKey + SECRET_KEY_SUFFIX).getBytes(StandardCharsets.UTF_8));
        return AUTHORIZATION_PREFIX + new String(encodedSecretKey);
    }

    public RestClient.Builder getBuilder(String name) {
        return builders.get(name);
    }
}
