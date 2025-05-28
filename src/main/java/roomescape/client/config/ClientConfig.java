package roomescape.client.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.TossPaymentsClient;

@Configuration
public class ClientConfig {

    private final String secretKey;

    public ClientConfig(@Value("${toss.payments.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public TossPaymentsClient tossRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);
        return new TossPaymentsClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/")
                        .requestFactory(requestFactory)
                        .defaultHeader("Authorization", getBasicAuthorizationValue())
                        .build()
        );
    }

    private String getBasicAuthorizationValue() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }
}
