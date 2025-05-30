package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossPaymentProperties;

@RequiredArgsConstructor
@EnableConfigurationProperties(TossPaymentProperties.class)
@Configuration
public class ClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    @Bean
    public RestClient tossPaymentRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(tossPaymentProperties.timeout());
        requestFactory.setReadTimeout(tossPaymentProperties.timeout());

        String encodedKey = Base64.getEncoder()
                .encodeToString((tossPaymentProperties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl(tossPaymentProperties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Authorization", "Basic " + encodedKey)
                .build();
    }
}
