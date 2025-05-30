package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;
import roomescape.reservation.service.PaymentRestClient;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private static final int CONNECT_TIMEOUT = 5_000;
    private static final int READ_TIMEOUT = 30 * 1_000;

    private final PaymentResponseErrorHandler paymentResponseErrorHandler;

    @Bean
    public PaymentRestClient paymentRestClient(
            @Value("${toss.secret-key}") String secretKey
    ) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        return new PaymentRestClient(
                restClient().baseUrl("https://api.tosspayments.com/v1/payments")
                        .requestFactory(requestFactory)
                        .defaultStatusHandler(paymentResponseErrorHandler)
                        .build(),
                secretKey
        );
    }

    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder();
    }
}
