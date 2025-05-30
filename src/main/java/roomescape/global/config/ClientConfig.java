package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.config.properties.TossProperties;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;
import roomescape.reservation.service.PaymentRestClient;

@Configuration
@EnableConfigurationProperties(TossProperties.class)
@RequiredArgsConstructor
public class ClientConfig {

    private final PaymentResponseErrorHandler paymentResponseErrorHandler;
    private final TossProperties tossProperties;

    @Bean
    public PaymentRestClient paymentRestClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(tossProperties.connectTimeout());
        requestFactory.setReadTimeout(tossProperties.readTimeout());

        return new PaymentRestClient(
                restClient().baseUrl("https://api.tosspayments.com/v1/payments")
                        .requestFactory(requestFactory)
                        .defaultStatusHandler(paymentResponseErrorHandler)
                        .build(),
                tossProperties.secretKey()
        );
    }

    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder();
    }
}
