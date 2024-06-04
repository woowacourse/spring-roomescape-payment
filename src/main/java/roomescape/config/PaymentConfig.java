package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentExceptionHandler;
import roomescape.infrastructure.PaymentProperties;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return (restClientBuilder) -> restClientBuilder
                .requestFactory(clientHttpRequestFactory())
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", createAuthorizationHeader())
                .defaultStatusHandler(responseErrorHandler());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(30L))
                .withReadTimeout(Duration.ofSeconds(60L));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String createAuthorizationHeader() {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentProperties.secretKey() + ":")
                        .getBytes(StandardCharsets.UTF_8));
        return  "Basic " + new String(encodedBytes);
    }

    private ResponseErrorHandler responseErrorHandler() {
        return new PaymentExceptionHandler();
    }
}
