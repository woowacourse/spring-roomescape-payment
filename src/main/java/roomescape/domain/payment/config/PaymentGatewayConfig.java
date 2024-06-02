package roomescape.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.pg.PaymentGateway;
import roomescape.domain.payment.pg.TossPaymentGateway;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static roomescape.domain.payment.config.PaymentApiUrl.PG_API_BASE_URL;

@Configuration
public class PaymentGatewayConfig {

    private static final long API_TIME_OUT_VALUE = 2L;

    @Value("${custom.pg.widget-secret-key}")
    private String widgetSecretKey;

    @Bean
    public PaymentGateway paymentGateway() {
        return new TossPaymentGateway(configRestClient());
    }

    private RestClient configRestClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofMinutes(API_TIME_OUT_VALUE));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(PG_API_BASE_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, generateAuthorizations());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    private String generateAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
