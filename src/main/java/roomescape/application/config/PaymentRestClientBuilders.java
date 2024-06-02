package roomescape.application.config;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.util.Base64Utils;

@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentRestClientBuilders {
    private final Map<String, RestClient.Builder> builders;

    public PaymentRestClientBuilders(PaymentClientProperties properties) {
        this.builders = properties.getNames()
                .stream()
                .collect(Collectors.toMap(name -> name, name -> createBuilder(properties.get(name))));
    }

    private Builder createBuilder(PaymentClientProperty property) {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(createRequestFactory(property)))
                .defaultHeader(HttpHeaders.AUTHORIZATION, Base64Utils.encode(property.secret() + ":"))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(new PaymentRequestResponseLoggingInterceptor())
                .requestInterceptor(new PaymentTimeoutHandlerInterceptor())
                .baseUrl(property.url());
    }

    private ClientHttpRequestFactory createRequestFactory(PaymentClientProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(property.connectionTimeoutInSeconds())
                .withReadTimeout(property.readTimeoutInSeconds());

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    public RestClient.Builder get(String name) {
        return builders.get(name);
    }
}
