package roomescape.infra.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    private static final String PROVIDER_TOSS = "toss";

    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClient tossPaymentsRestClient() {
        PaymentClientProviderProperties properties = paymentClientProperties.getProvider(PROVIDER_TOSS);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(properties.connectionTimeoutInSeconds())
                .setReadTimeout(properties.readTimeoutInSeconds())
                .build();

        return RestClient.builder(restTemplate)
                .requestFactory(new BufferingClientHttpRequestFactory(createRequestFactory(properties)))
                .requestInterceptor(new PaymentClientLoggingInterceptor())
                .defaultHeader(HttpHeaders.AUTHORIZATION, encodeSecretKey(properties.secretKey()))
                .baseUrl(properties.baseUrl())
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(PaymentClientProviderProperties property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(property.connectionTimeoutInSeconds())
                .withReadTimeout(property.readTimeoutInSeconds());

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
