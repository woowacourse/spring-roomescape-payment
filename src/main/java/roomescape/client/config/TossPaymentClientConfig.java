package roomescape.client.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentClientConfig {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClientConfig.class);
    private final String secretKey;

    public TossPaymentClientConfig(@Value("${toss-payments.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }


    @Bean
    public RestClient.Builder restClientBuilder() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json");
    }

    @Bean
    public RestClient tossRestClient(RestClient.Builder restClientBuilder) {
        log.info("tossClient call");
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return restClientBuilder
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", encodedAuth)
                .build();
    }
}
