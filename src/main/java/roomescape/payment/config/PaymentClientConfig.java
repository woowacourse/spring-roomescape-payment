package roomescape.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Configuration
public class PaymentClientConfig {

    private static final int CONNECT_TIMEOUT_SECONDS = 3;
    private static final int READ_TIMEOUT_SECONDS = 33;

    @Value("${payment.api.base-url}")
    private String baseUrl;
    @Value("${payment.auth.scheme}")
    private String authScheme;
    @Value("${payment.secret.key}")
    private String secretKey;

    @Bean
    public RestClient getRestClient() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = authScheme + " " + new String(encodedBytes);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", authorizations)
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    private SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));
        return requestFactory;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("Payment API 요청: {} {} | Body: {}",
                    request.getMethod(),
                    request.getURI(),
                    new String(body, StandardCharsets.UTF_8));

            ClientHttpResponse response = execution.execute(request, body);

            log.debug("Payment API 응답: {}", response.getStatusCode());

            return response;
        };
    }
}
