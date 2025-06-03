package roomescape.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean
    @Qualifier("tossPaymentRestClient")
    public RestClient tossPaymentRestClient(
            @Value("${toss.payment.base-url}") String baseUrl,
            @Value("${toss.payment.secret-key}") String secretKey,
            @Value("${toss.payment.auth-scheme}") String authScheme,
            @Value("${toss.payment.timeout.connect}") int connectTimeout,
            @Value("${toss.payment.timeout.read}") int readTimeout
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory(connectTimeout, readTimeout))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", createAuthorization(secretKey, authScheme))
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    private SimpleClientHttpRequestFactory createRequestFactory(final int connectTimeout, final int readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeout));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeout));
        return requestFactory;
    }

    private String createAuthorization(final String secretKey, final String authScheme) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return authScheme + " " + new String(encodedBytes);
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
