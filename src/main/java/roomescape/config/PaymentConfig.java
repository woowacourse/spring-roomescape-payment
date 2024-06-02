package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.exception.PaymentExceptionHandler;
import roomescape.service.PaymentService;

@Configuration
public class PaymentConfig {
    @Value("${payment.toss.secret-key}")
    private String widgetSecretKey;

    @Bean
    public PaymentService paymentService(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(PaymentService.class);
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return (restClientBuilder) -> restClientBuilder
                .requestFactory(clientHttpRequestFactory())
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorization())
                .defaultStatusHandler(responseErrorHandler())
                .baseUrl("https://api.tosspayments.com");
    }

    private String authorization() {
        byte[] encodedBytes = Base64.getEncoder().encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(30));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private ResponseErrorHandler responseErrorHandler() {
        return new PaymentExceptionHandler();
    }
}
