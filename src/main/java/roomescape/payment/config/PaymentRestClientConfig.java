package roomescape.payment.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentRestClientConfig {

    @Bean
    public RestClient tossRestClient(
            RestClient.Builder builder,
            @Value("${toss.connectionTimeout}") int connectionTimeMs,
            @Value("${toss.readTimeout}") int readTimeMs,
            @Value("${toss.secret-key}") String secretKey
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeMs);
        factory.setReadTimeout(readTimeMs);

        return builder
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
                .requestFactory(factory)
                .build();
    }
}

