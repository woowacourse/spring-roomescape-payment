package roomescape.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentExceptionHandler;
import roomescape.infrastructure.PaymentClient;
import roomescape.infrastructure.PaymentProperties;
import roomescape.infrastructure.TossPaymentClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public PaymentClient paymentClient() {
        return new TossPaymentClient(restClient());
    }

    private RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", authorizationHeader())
                .defaultStatusHandler(exceptionHandler())
                .requestFactory(requestFactory())
                .build();
    }

    private ResponseErrorHandler exceptionHandler() {
        return new PaymentExceptionHandler();
    }

    private String authorizationHeader() {
        String credentials = paymentProperties.secretKey() + ":";
        return Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private ClientHttpRequestFactory requestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3L))
                .withReadTimeout(Duration.ofSeconds(30L));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
