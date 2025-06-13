package roomescape.payment.infrastructure.client;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class PaymentClientConfig {

    private final TossPaymentProperties tossPaymentProperties;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    @Bean
    public TossPaymentRestClient paymentRestClient() {
        String base64Auth = Base64.getEncoder()
                .encodeToString(tossPaymentProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        return new TossPaymentRestClient(
                RestClient.builder()
                        .baseUrl(tossPaymentProperties.getBaseUrl())
                        .defaultHeader("Authorization", "Basic " + base64Auth)
                        .requestFactory(createRequestFactory())
                        .build(),
                tossPaymentClientErrorHandler
        );
    }

    // TODO : 커넥션 풀 설정 추가 (최대 연결 수, keep-alive 등)
    private ClientHttpRequestFactory createRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(tossPaymentProperties.getConnectionTimeout()));
        factory.setReadTimeout(Duration.ofSeconds(tossPaymentProperties.getReadTimeout()));

        return factory;
    }
}
