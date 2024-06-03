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
import roomescape.exception.handler.PaymentExceptionHandler;
import roomescape.infrastructure.TossPaymentProperties;

import java.time.Duration;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentConfig {

    public static final long CONNECTION_TIMEOUT = 3L;
    public static final long READ_TIMEOUT = 30L;

    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentConfig(final TossPaymentProperties tossPaymentProperties) {
        this.tossPaymentProperties = tossPaymentProperties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", authorizationHeader())
                .defaultStatusHandler(paymentExceptionHandler())
                .requestFactory(requestFactory())
                .build();
    }


    private String authorizationHeader() {
        String secretKey = tossPaymentProperties.secretKey() + ":";
        String credentials = Base64.getEncoder()
                .encodeToString((secretKey).getBytes());

        return "Basic " + credentials;
    }

    private ResponseErrorHandler paymentExceptionHandler() {
        return new PaymentExceptionHandler();
    }

    private ClientHttpRequestFactory requestFactory() {
        ClientHttpRequestFactorySettings timeOutSetting = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, timeOutSetting);
    }
}
