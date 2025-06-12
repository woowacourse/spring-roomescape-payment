package roomescape.payment.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.TossPaymentInterceptor;
import roomescape.payment.client.handler.TossClientErrorHandler;
import roomescape.payment.client.handler.TossServerErrorHandler;

@Configuration
public class TossRestClientConfiguration {

    private final TossPaymentInterceptor tossPaymentInterceptor;
    private final TossServerErrorHandler tossServerErrorHandler;
    private final TossClientErrorHandler tossClientErrorHandler;

    @Value("${payment.toss.base-url}")
    private String baseUrl;

    public TossRestClientConfiguration(TossPaymentInterceptor tossPaymentInterceptor,
                                       TossServerErrorHandler tossServerErrorHandler,
                                       TossClientErrorHandler tossClientErrorHandler) {
        this.tossPaymentInterceptor = tossPaymentInterceptor;
        this.tossServerErrorHandler = tossServerErrorHandler;
        this.tossClientErrorHandler = tossClientErrorHandler;
    }

    @Bean
    public RestClient restClient() {
        ClientHttpRequestFactory requestFactory = createRequestFactory(createRequestSettings());
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(tossPaymentInterceptor)
                .requestFactory(requestFactory)
                .defaultStatusHandler(tossServerErrorHandler)
                .defaultStatusHandler(tossClientErrorHandler)
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactorySettings settings) {
        return ClientHttpRequestFactoryBuilder
                .simple()
                .build(settings);
    }

    private ClientHttpRequestFactorySettings createRequestSettings() {
        return ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofSeconds(1))
                .withReadTimeout(Duration.ofSeconds(5));
    }
}
