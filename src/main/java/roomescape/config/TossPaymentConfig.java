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

    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentConfig(TossPaymentProperties tossPaymentProperties) {
        this.tossPaymentProperties = tossPaymentProperties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl(tossPaymentProperties.url().base())
                .defaultHeader("Authorization", authorizationHeader())
                .defaultStatusHandler(new PaymentExceptionHandler())
                .requestFactory(requestFactory())
                .build();
    }

    private String authorizationHeader() {
        String secretKey = tossPaymentProperties.secretKey() + ":";
        String credentials = Base64.getEncoder()
                .encodeToString((secretKey).getBytes());
        
        return "Basic " + credentials;
    }

    private ClientHttpRequestFactory requestFactory() {
        final Long timeOutConnection = tossPaymentProperties.timeOut().connection();
        final Long timeOutRead = tossPaymentProperties.timeOut().read();

        ClientHttpRequestFactorySettings timeOutSetting = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(timeOutConnection))
                .withReadTimeout(Duration.ofSeconds(timeOutRead));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, timeOutSetting);
    }
}
