package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossPaymentProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
@RequiredArgsConstructor
public class ClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    @Bean(name = "tossPaymentRestClient")
    public RestClient tossPaymentRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(tossPaymentProperties.connectionTimeout());
        requestFactory.setReadTimeout(tossPaymentProperties.readTimeout());

        final String encodedKey = Base64.getEncoder()
                .encodeToString((tossPaymentProperties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl(tossPaymentProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .requestFactory(requestFactory)
                .build();
    }
}
