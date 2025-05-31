package roomescape.reservation.config;

import java.time.Duration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("production")
public class TossRestClientConfiguration {

    private final TossPaymentInterceptor tossPaymentInterceptor;

    public TossRestClientConfiguration(TossPaymentInterceptor tossPaymentInterceptor) {
        this.tossPaymentInterceptor = tossPaymentInterceptor;
    }

    @Bean
    public RestClient restClient() {
        ClientHttpRequestFactory requestFactory = createRequestFactory(createRequestSettings());
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestInterceptor(tossPaymentInterceptor)
                .requestFactory(requestFactory)
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactorySettings settings) {
        return ClientHttpRequestFactoryBuilder
                .simple()
                .build(settings);
    }

    private ClientHttpRequestFactorySettings createRequestSettings() {
        return ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofSeconds(2))
                .withReadTimeout(Duration.ofSeconds(3));
    }
}
