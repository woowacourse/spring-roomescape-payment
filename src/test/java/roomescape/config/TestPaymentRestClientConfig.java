package roomescape.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;


public class TestPaymentRestClientConfig {

    @Bean
    public RestClient tossRestClient(
            RestClient.Builder builder,
            @Value("${toss.timeout:3000}") int timeoutMs,
            @Value("${toss.secret-key}") String secretKey
    ) {
        return builder
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
                .build();
    }
}

