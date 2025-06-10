package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossPaymentProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
@RequiredArgsConstructor
public class ClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    @Bean(name = "tossPaymentRestClient")
    public RestClient tossPaymentRestClient() {
        return RestClient.builder()
                .baseUrl(tossPaymentProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + getEncodedKey())
                .requestFactory(getFactory())
                .build();
    }

    private String getEncodedKey() {
        return Base64.getEncoder()
                .encodeToString((tossPaymentProperties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
    }

    private HttpComponentsClientHttpRequestFactory getFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(tossPaymentProperties.connectionTimeout());
        factory.setReadTimeout(tossPaymentProperties.readTimeout());
        return factory;
    }
}
