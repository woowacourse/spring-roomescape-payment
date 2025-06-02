package roomescape.payment.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public RestClientCustomizer restClientCustomizer(
            @Value("${toss.timeout:3000}") int timeoutMs,
            @Value("${toss.secret-key}") String secretKey
    ) {
        return builder -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(timeoutMs);
            factory.setReadTimeout(timeoutMs);
            builder
                    .baseUrl("https://api.tosspayments.com/v1")
                    .defaultHeader(HttpHeaders.AUTHORIZATION,
                            "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
                    .requestFactory(factory);
        };
    }
}
